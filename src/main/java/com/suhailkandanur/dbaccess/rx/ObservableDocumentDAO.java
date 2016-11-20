package com.suhailkandanur.dbaccess.rx;

import com.suhailkandanur.entity.Document;
import rx.Observable;

import java.util.List;

/**
 * Created by suhail on 2016-11-18.
 */
public interface ObservableDocumentDAO {

    Observable<Document> getDocumentList(int storeId, boolean includeInActive);

    default Observable<Document> getDocumentList(int storeId) {
        return getDocumentList(storeId, false);
    }

    Observable<Document> getDocument(int storeId, String docId);

    Observable<Document> createDocument(int storeId, String docId, String docIdSha1, String requester);
}
