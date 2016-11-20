package com.suhailkandanur.filesystem.impl;

import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.DocumentVersion;
import com.suhailkandanur.entity.Repo;
import com.suhailkandanur.entity.Store;
import com.suhailkandanur.filesystem.FileSystemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.sun.tools.doclint.Entity.rho;

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
        //String file, docid, version, docidsha1, storeLocation;
        //create object
        String fileSha1 = version.getSha1Cksum();
        Objects.requireNonNull(fileSha1);

        final Path srcFilePath = Paths.get(srcFileLocation);
        if (Files.notExists(srcFilePath)) {
            logger.error("source file does not exist at {}", srcFileLocation);
            throw new Exception("source file does not exist at " + srcFileLocation);
        }
        final Path documentObjectPath = Paths.get(storeLocation, "objects", fileSha1.substring(0, 2), fileSha1.substring(2, 6), fileSha1.substring(6));
        if (Files.exists(documentObjectPath)) {
            logger.info("document already exists at {} ", documentObjectPath.toString());
        } else {
            Files.copy(srcFilePath, documentObjectPath);
        }


    }
}
