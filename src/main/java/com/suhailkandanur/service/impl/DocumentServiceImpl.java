package com.suhailkandanur.service.impl;

import com.suhailkandanur.dbaccess.DocumentDAO;
import com.suhailkandanur.dbaccess.DocumentVersionDAO;
import com.suhailkandanur.dbaccess.StoreDAO;
import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.DocumentVersion;
import com.suhailkandanur.service.DocumentService;
import com.suhailkandanur.util.ChecksumUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.SQLDataException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by suhail on 2016-11-08.
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private DocumentVersionDAO documentVersionDAO;

    @Autowired
    private StoreDAO storeDAO;

    private Supplier<String> documentIdGenerator = () -> UUID.randomUUID().toString();
    private Function<Document, Document> populateDocumentVersions = document -> document.addVersions(documentVersionDAO.getDocumentVersions(document.getDocRef()));

    @Override
    @Transactional
    public Document createDocument(String filePath, int storeId, String requester) throws IOException {
        Objects.requireNonNull(requester);
        Objects.requireNonNull(filePath);
        if (!storeDAO.exists(storeId)) {
            logger.error("store with storeid '{}' does not exist", storeId);
            return null;
        }

        String docId = documentIdGenerator.get();
        String docIdSha1 = ChecksumUtils.sha1(docId);


        final Document document = documentDAO.createDocument(storeId, docId, docIdSha1, requester);
        return createDocumentVersion(document, filePath, requester);
    }

    private Document createDocumentVersion(final Document document, final String filePath, final String requester) throws IOException {
        Objects.requireNonNull(document);
        Objects.requireNonNull(filePath);
        Objects.requireNonNull(requester);

        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("supplied file path '{}' does not exist or unreadable", filePath);
            return null;
        }
        String sha1Cksum = ChecksumUtils.sha1(file);
        long fileSize = file.length();
        int docRef = document.getDocRef();
        long version = System.currentTimeMillis();
        String mimeType = new Tika().detect(file);
        final DocumentVersion documentVersion = documentVersionDAO.createDocumentVersion(docRef, version, file.getName(), fileSize, mimeType, sha1Cksum, requester);
        if(documentVersion == null) {
            logger.error("unable to create document version");
            throw new IOException("unable to create document version");
        }
        return populateDocumentVersions.apply(document);
    }

    @Override
    @Transactional
    public Document updateDocument(String filePath, int storeId, String docId, String requester) throws IOException {

        Objects.requireNonNull(requester);
        Objects.requireNonNull(filePath);
        Objects.requireNonNull(docId);

        //validate params and populate defaults
        final Document document = documentDAO.getDocument(storeId, docId);

        if (document == null) {
            logger.error("supplied document with docid '{}' does not exist", docId);
            return null;
        }

        return createDocumentVersion(document, filePath, requester);
    }

    @Override
    public File documentAsFile(int storeId, String docId) {
        return null;
    }

    @Override
    public List<Document> getDocumentListFromStore(int storeId) {
        final List<Document> documentList = documentDAO.getDocumentList(storeId);
        return Optional.ofNullable(documentList).orElse(Collections.emptyList())
                .stream()
                .map(populateDocumentVersions)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public Document getDocumentFromStore(int storeId, String docId) {
        return Optional.ofNullable(documentDAO.getDocument(storeId, docId)).map(populateDocumentVersions).orElse(null);
    }

    // @Override
    // public List<Document> getDocumentsFromStore(int storeId, int docRef) {
    //     return null;
    // }
}
