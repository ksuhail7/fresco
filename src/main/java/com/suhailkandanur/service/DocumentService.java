package com.suhailkandanur.service;

import com.suhailkandanur.entity.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by suhail on 2016-11-07.
 */
public interface DocumentService {
    Document createDocument(String filePath, int storeId, String requester) throws IOException;

    Document updateDocument(String filePath, int storeId, String docId, String requester) throws IOException;

    File documentAsFile(int storeId, String docId);

    List<Document> getDocumentListFromStore(int storeId);

    Document getDocumentFromStore(int storeId, String docId);

    //Document getDocumentsFromStore(int storeId, int docRef);
}
