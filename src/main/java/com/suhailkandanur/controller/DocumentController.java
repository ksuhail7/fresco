package com.suhailkandanur.controller;

import com.suhailkandanur.dbaccess.DocumentDAO;
import com.suhailkandanur.entity.Document;
import com.suhailkandanur.service.DocumentService;
import com.suhailkandanur.util.ChecksumUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.suhailkandanur.util.ChecksumUtils.sha1;

/**
 * Created by suhail on 2016-11-06.
 */
@RestController
@RequestMapping("/fresco/document")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "/{storeId}/{docRef}", method = RequestMethod.GET)
    public List<Document> getDocumentInStore(@PathVariable int storeId, @PathVariable int docRef) {
        return documentDAO.getDocumentsFromStore(storeId, docRef);
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    public List<Document> getAllDocumentsInStore(@PathVariable int storeId) {
        return documentDAO.getAllDocumentsInStore(storeId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public int createDocument(String filePath, int storeId, String docId) {
        String requester = System.getProperty("user.name");
        try {
            File fileObject = new File(filePath);
            String fileCksum = ChecksumUtils.sha1(fileObject);
            long version = Instant.now().toEpochMilli();
            docId = Optional.ofNullable(docId).orElse(UUID.randomUUID().toString());
            String docIdSha1 = ChecksumUtils.sha1(docId);
            String fileName = fileObject.getName();
            String mimeType = new Tika().detect(fileObject);
            long fileSize = fileObject.length();
            Date now = new Date();
            int docRef = documentDAO.createDocument(docId, storeId, docIdSha1, version, fileName, fileSize, mimeType, fileCksum, requester, true);
            return docRef;
        } catch(IOException e) {
            logger.error("unable to create document, error: {}", e.getMessage());
            return -1;
        }
       // return documentDAO.createDocument();
    }

    @RequestMapping(value="/retrieve/{storeId}/{docRef}", method = RequestMethod.GET)
    public Response retrieveFile(@PathVariable int storeId, @PathVariable int docRef) throws IOException {
        File file = documentService.documentAsFile(storeId, docRef);
        return Response.ok(file, new Tika().detect(file) )
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
                .build();
    }
}

