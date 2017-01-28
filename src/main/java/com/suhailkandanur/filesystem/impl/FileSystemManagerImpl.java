package com.suhailkandanur.filesystem.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.DocumentVersion;
import com.suhailkandanur.entity.Store;
import com.suhailkandanur.filesystem.FileSystemManager;
import com.suhailkandanur.util.ChecksumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Created by suhail on 2016-11-15.
 */
@Component
public class FileSystemManagerImpl implements FileSystemManager {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemManagerImpl.class);
    @Override
    @Transactional
    public void createRepository(String rootPath, String repoName) throws Exception {
        Path repositoryRoot = Paths.get(rootPath);
        if(Files.notExists(repositoryRoot))
            throw new IOException("root folder " + rootPath + " does not exist");
        Path repositoryPath = repositoryRoot.resolve(repoName);
        if(Files.notExists(repositoryPath)) Files.createDirectories(repositoryPath);
        Path storesRoot = repositoryPath.resolve("store");
        if (Files.notExists(storesRoot)) {
            Files.createDirectories(storesRoot);
        }
    }

    @Override
    @Transactional
    public void createStore(String repositoryLocation, String storeName) throws Exception {
        Path repositoryPath = Paths.get(repositoryLocation);
        if (Files.notExists(repositoryPath)) {
            throw new Exception("repository path " + repositoryPath.toString() + " does not exist");
        }
        Path storePath = repositoryPath.resolve("store").resolve(storeName);
        if (Files.notExists(storePath)) {
            Files.createDirectories(storePath);
        }
        Path objectsPath = storePath.resolve("objects");
        if (Files.notExists(objectsPath)) {
            Files.createDirectories(objectsPath);
        }
        Path docsPath = storePath.resolve("documents");
        if (Files.notExists(docsPath)) {
            Files.createDirectories(docsPath);
        }
    }

    @Override
    @Transactional
    public void createDocument(Store store, Document document, DocumentVersion version, String srcFileLocation, String storeLocation) throws Exception {

    }

    public void createDocument(String storeLocation, String srcFileLocation, long version, String docId) throws Exception {
        //required parameters
        //1. srcFileLocation
        //2. fileSha1
        //3. version
        //4. docIdSha1
        //5. docId
        //String file, docid, version, docidsha1, storeLocation;
        //create object
        final Path srcFilePath = Paths.get(srcFileLocation);
        if (Files.notExists(srcFilePath)) {
            logger.error("source file does not exist at {}", srcFileLocation);
            throw new Exception("source file does not exist at " + srcFileLocation);
        }
        String fileSha1 = ChecksumUtils.sha1(new File(srcFileLocation));
        Objects.requireNonNull(fileSha1);

        assert fileSha1.length() == 40;
        //copy the file
        final Path documentObjectPath = Paths.get(storeLocation, "objects", fileSha1.substring(0, 2), fileSha1.substring(2, 6), fileSha1.substring(6));
        if (Files.exists(documentObjectPath)) {
            logger.warn("document already exists at {} ", documentObjectPath.toString());
        } else {
            if(Files.notExists(documentObjectPath.getParent())) Files.createDirectories(documentObjectPath.getParent());
            Files.copy(srcFilePath, documentObjectPath);
        }

        //create document metadata
        String docIdSha1 = ChecksumUtils.sha1(docId);
        Objects.requireNonNull(docIdSha1);
        assert docIdSha1.length() == 40;
        final Path docMetadataPath = Paths.get(storeLocation, "documents", docIdSha1.substring(0, 2), docIdSha1.substring(2, 6), docIdSha1.substring(6));
        if(Files.notExists(docMetadataPath))
            Files.createDirectories(docMetadataPath);
        final Path versionPath = docMetadataPath.resolve(Long.toString(version));
        if (Files.exists(versionPath)) {
            logger.error("document version {} already exists at path, unable to create new", version, versionPath.toString());
            throw new Exception("document version already exists");
        }

        ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("docid", docId);
        objectNode.put("docid_sha1", docIdSha1);
        objectNode.put("filename", srcFilePath.getFileName().toString());
        objectNode.put("object", fileSha1);
        objectNode.put("filesize", srcFilePath.toFile().length());
        mapper.writeValue(versionPath.toFile(), objectNode);
        logger.info("document version successfully created {}", versionPath.toString());
        final Path currentVersionPath = docMetadataPath.resolve("current");
        try {
            Files.createSymbolicLink(currentVersionPath, versionPath);
        } catch(UnsupportedOperationException ex) {
            logger.error("unable to create symlink, error: {}", ex.getMessage());
            throw ex;
        }
    }


    public static void main(String[] args) throws Exception {
        String storeLocation = "/Users/suhail/tmp/fresco/fs1/testRepo0/store/0";
        String srcFileLocation = "/Users/suhail/tmp/yolinux-mime-test.png";
        String docId = "b6bdef6c-ccf1-4717-804d-dff29a97e0fc";
        long version = System.currentTimeMillis();
        new FileSystemManagerImpl().createDocument(storeLocation, srcFileLocation, version, docId);
    }
}
