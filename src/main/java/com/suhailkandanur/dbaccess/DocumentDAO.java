package com.suhailkandanur.dbaccess;

/**
 * Created by suhail on 2016-11-10.
 */
public interface DocumentDAO {
    boolean exists(int storeId, String docId);

    default boolean notExists(int storeId, String docId) {
        return !exists(storeId, docId);
    }
}
