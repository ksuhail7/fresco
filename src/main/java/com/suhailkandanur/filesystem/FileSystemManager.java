package com.suhailkandanur.filesystem;

import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.DocumentVersion;
import com.suhailkandanur.entity.Repo;
import com.suhailkandanur.entity.Store;

/**
 * Created by suhail on 2016-11-13.
 */
public interface FileSystemManager {
    void createRepository(String rootPath, String repoName) throws Exception;
    void createStore(String repositoryLocation, String storeName) throws Exception;
    void createDocument(Store store, Document document, DocumentVersion version, String srcFileLocation, String storeLocation) throws Exception;

}
