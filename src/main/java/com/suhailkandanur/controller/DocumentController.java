package com.suhailkandanur.controller;

import com.suhailkandanur.entity.Document;
import com.suhailkandanur.service.DocumentService;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by suhail on 2016-11-06.
 */
@RestController
@RequestMapping("/fresco/document")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    // @RequestMapping(value = "/{storeId}/{docRef}", method = RequestMethod.GET)
    // public Document getDocumentInStore(@PathVariable int storeId, @PathVariable int docRef) {
    //     return documentService.get(storeId, docRef);
    // }

    @RequestMapping(value = "/{storeId}/{docId}", method = RequestMethod.GET)
    public Document getDocumentInStore(@PathVariable int storeId, @PathVariable String docId) {
        return documentService.getDocumentFromStore(storeId, docId);
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    public List<Document> getAllDocumentsInStore(@PathVariable int storeId) {
        return documentService.getDocumentListFromStore(storeId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Document createDocument(String filePath, Integer storeId, String requester) {
        requester = Optional.ofNullable(requester).orElse(System.getProperty("user.name"));
        try {
            return documentService.createDocument(filePath, storeId, requester);
        } catch (IOException ioe) {
            logger.error("unable to create document, error: {}", ioe.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/{storeId}/{docId}", method = RequestMethod.PUT)
    public Document updateDocument(@PathVariable int storeId, @PathVariable String docId, @RequestBody String filePath) {
        String requester = null;
        requester = Optional.ofNullable(requester).orElse(System.getProperty("user.name"));
        try {
            return documentService.updateDocument(filePath, storeId, docId, requester);
        } catch (IOException e) {
            logger.error("unable to update document, error: {}", e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/retrieve/{storeId}/{docId}", method = RequestMethod.GET)
    public Response retrieveFile(@PathVariable int storeId, @PathVariable String docId) throws IOException {
        File file = documentService.documentAsFile(storeId, docId);
        return Response.ok(file, new Tika().detect(file))
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"") //optional
                .build();
    }
}


