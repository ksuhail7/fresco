package com.suhailkandanur.service;

import com.suhailkandanur.entity.Document;

import java.io.File;
import java.util.List;

/**
 * Created by suhail on 2016-11-07.
 */
public interface DocumentService {
    String createOrUpdateDocument(String filePath, int storeId, String docId, String requester);

    File documentAsFile(int storeId, String docId);

    List<Document> getDocumentListFromStore(int storeId);

    List<Document> getDocumentFromStore(int storeId, String docId);

    List<Document> getDocumentsFromStore(int storeId, int docRef);
}
