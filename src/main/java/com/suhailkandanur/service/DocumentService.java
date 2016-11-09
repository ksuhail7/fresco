package com.suhailkandanur.service;

import java.io.File;

/**
 * Created by suhail on 2016-11-07.
 */
public interface DocumentService {
    void createDocument();

    File documentAsFile(int storeId, int docRef);
}
