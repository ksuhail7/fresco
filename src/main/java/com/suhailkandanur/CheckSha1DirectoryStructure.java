package com.suhailkandanur;

import org.apache.commons.codec.digest.DigestUtils;
import sun.text.normalizer.Trie;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by suhail on 2017-01-16.
 */
public class CheckSha1DirectoryStructure {

    static class PathTrie {
        TrieNode rootNode;

        public PathTrie() {
            this.rootNode = new TrieNode(null);
        }

        /**
         * add a path to the path trie
         *
         * @param path
         */
        public void addPath(String path) {
            if (path == null) {
                return;
            }
            String[] pathComponents = path.split("/");
            TrieNode parent = rootNode;
            String part = null;
            if (pathComponents.length <= 1) {
                throw new IllegalArgumentException("Invalid path " + path);
            }
            for (int i = 1; i < pathComponents.length; i++) {
                part = pathComponents[i];
                if (parent.getChild(part) == null) {
                    parent.addChild(part, new TrieNode(parent));
                }
                parent = parent.getChild(part);
            }
            parent.setProperty(true);
        }

        public Map<Integer, Integer> levelCountMap() {
            return levelCountMap(this.rootNode, 0);
        }

        private Map<Integer, Integer> levelCountMap(TrieNode node, int index) {
            if(node == null)
                return Collections.emptyMap();
            Map<Integer, Integer> countMap = new HashMap<>();
            String[] children = node.getChildren();
            countMap.put(index, children.length);
            for(String child: children) {
                TrieNode childNode = node.getChild(child);
                Map<Integer, Integer> newMap = levelCountMap(childNode, index+1);
                for(int level : newMap.keySet()) {
                    int maxCount = countMap.getOrDefault(level, 0);
                    if (maxCount < newMap.get(level)) {
                        maxCount = newMap.get(level);
                    }
                    countMap.put(level, maxCount);
                }
            }
            return countMap;
        }
    }


    static class TrieNode {
        boolean property = false;
        final HashMap<String, TrieNode> children;
        TrieNode parent = null;

        /**
         * create a trienode with parent
         * as parameter
         *
         * @param parent the parent of this trienode
         */
        private TrieNode(TrieNode parent) {
            children = new HashMap<String, TrieNode>();
            this.parent = parent;
        }

        /**
         * get the parent of this node
         *
         * @return the parent node
         */
        TrieNode getParent() {
            return this.parent;
        }

        /**
         * set the parent of this node
         *
         * @param parent the parent to set to
         */
        void setParent(TrieNode parent) {
            this.parent = parent;
        }

        /**
         * a property that is set
         * for a node - making it
         * special.
         */
        void setProperty(boolean prop) {
            this.property = prop;
        }

        /**
         * the property of this
         * node
         *
         * @return the property for this
         * node
         */
        boolean getProperty() {
            return this.property;
        }

        /**
         * add a child to the existing node
         *
         * @param childName the string name of the child
         * @param node      the node that is the child
         */
        void addChild(String childName, TrieNode node) {
            synchronized (children) {
                if (children.containsKey(childName)) {
                    return;
                }
                children.put(childName, node);
            }
        }

        /**
         * delete child from this node
         *
         * @param childName the string name of the child to
         *                  be deleted
         */
        void deleteChild(String childName) {
            synchronized (children) {
                if (!children.containsKey(childName)) {
                    return;
                }
                TrieNode childNode = children.get(childName);
                // this is the only child node.
                if (childNode.getChildren().length == 1) {
                    childNode.setParent(null);
                    children.remove(childName);
                } else {
                    // their are more child nodes
                    // so just reset property.
                    childNode.setProperty(false);
                }
            }
        }

        /**
         * return the child of a node mapping
         * to the input childname
         *
         * @param childName the name of the child
         * @return the child of a node
         */
        TrieNode getChild(String childName) {
            synchronized (children) {
                if (!children.containsKey(childName)) {
                    return null;
                } else {
                    return children.get(childName);
                }
            }
        }

        /**
         * get the list of children of this
         * trienode.
         *
         * @return the string list of its children
         */
        String[] getChildren() {
            synchronized (children) {
                return children.keySet().toArray(new String[0]);
            }
        }
    }

    public static void main(String[] args) {
        PathTrie pathTrie = new PathTrie();

        int SAMPLE_COUNT=10_000_000;
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            String randomDocId = UUID.randomUUID().toString();
            String sha1 = DigestUtils.sha1Hex(randomDocId);
            String sha1Path = Paths.get("/", sha1.substring(0,2), sha1.substring(2, 4), sha1.substring(4)).toString();
            pathTrie.addPath(sha1Path);
            //System.out.println(sha1Path);
        }
        //pathTrie.addPath("/ms/user/k1/kadanurs");
        //pathTrie.addPath("/ms/user/k/kadanurs1");
        //pathTrie.addPath("/ms/group/k/kadanurs");

        Map<Integer, Integer> countMap = pathTrie.levelCountMap();
        for (int level : countMap.keySet()) {
            System.out.println("level " + level + " has max " + countMap.get(level) + " entries");
        }
    }
}
