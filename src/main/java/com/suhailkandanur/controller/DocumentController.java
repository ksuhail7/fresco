package com.suhailkandanur.controller;

import com.suhailkandanur.entity.Document;
import com.suhailkandanur.service.DocumentService;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.suhailkandanur.util.ChecksumUtils.sha1;

/**
 * Created by suhail on 2016-11-06.
 */
@RestController
@RequestMapping("/fresco/document")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "/{storeId}/{docRef}", method = RequestMethod.GET)
    public List<Document> getDocumentInStore(@PathVariable int storeId, @PathVariable int docRef) {
        return documentService.getDocumentsFromStore(storeId, docRef);
    }

    @RequestMapping(value="/{storeId}/{docId}", method = RequestMethod.GET)
    public List<Document> getDocumentInStore(@PathVariable int storeId, @PathVariable String docId) {
        return documentService.getDocumentFromStore(storeId, docId);
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    public List<Document> getAllDocumentsInStore(@PathVariable int storeId) {
        return documentService.getDocumentListFromStore(storeId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createDocument(String filePath, int storeId, String docId, String requester) {

            return documentService.createOrUpdateDocument(filePath, storeId, docId, requester);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String updateDocument(String filePath, int storeId, String docId, String requester) {
        return documentService.createOrUpdateDocument(filePath, storeId, docId, requester);
    }

    @RequestMapping(value = "/retrieve/{storeId}/{docRef}", method = RequestMethod.GET)
    public Response retrieveFile(@PathVariable int storeId, @PathVariable String docId) throws IOException {
        File file = documentService.documentAsFile(storeId, docId);
        return Response.ok(file, new Tika().detect(file))
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"") //optional
                .build();
    }
}

