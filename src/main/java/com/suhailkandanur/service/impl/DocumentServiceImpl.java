package com.suhailkandanur.service.impl;

import com.suhailkandanur.dbaccess.DocumentDAO;
import com.suhailkandanur.dbaccess.impl.DocumentDAOImpl;
import com.suhailkandanur.entity.Document;
import com.suhailkandanur.service.DocumentService;
import com.suhailkandanur.util.ChecksumUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by suhail on 2016-11-08.
 */
@Service
public class DocumentServiceImpl implements DocumentService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentDAO documentDAO;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public String createOrUpdateDocument(String filePath, int storeId, String docId, String requester) {

        //validate params and populate defaults
        requester = Optional.ofNullable(requester).orElse(System.getProperty("user.name"));
        if(docId != null && documentDAO.notExists(storeId, docId)) {
            logger.error("supplied document with docid '{}' does not exist", docId);
            return null;
        }
        docId = Optional.ofNullable(docId).orElse(UUID.randomUUID().toString());
        String docIdSha1 = ChecksumUtils.sha1(docId);

        //extract file parameters
        try {
            File f = new File(filePath);
            if (f == null || !f.exists() || !f.canRead()) {
                logger.error("specified file '{}' does not exists or not readable", filePath);
                return null;
            }
            long fileSize = f.length();
            String fileSha1Cksum = ChecksumUtils.sha1(f);
            String mimeType = new Tika().detect(f);

            return docId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File documentAsFile(int storeId, String docId) {
        return null;
    }

    @Override
    public List<Document> getDocumentListFromStore(int storeId) {
        return null;
    }

    @Override
    public List<Document> getDocumentFromStore(int storeId, String docId) {
        return null;
    }

    @Override
    public List<Document> getDocumentsFromStore(int storeId, int docRef) {
        return null;
    }
}
