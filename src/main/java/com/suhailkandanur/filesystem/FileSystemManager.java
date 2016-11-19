package com.suhailkandanur.filesystem;

import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.Repo;
import com.suhailkandanur.entity.Store;

/**
 * Created by suhail on 2016-11-13.
 */
public interface FileSystemManager {
    void createRepository(String rootPath, String repoName);
    void createStore(Repo repository, String storeName);
    void creteDocument(Store store, Document document);

}
