package com.suhailkandanur.controller;

import com.suhailkandanur.dbaccess.DocumentDAO;
import com.suhailkandanur.entity.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by suhail on 2016-11-06.
 */
@RestController
@RequestMapping("/fresco/document")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentDAO documentDAO;

    @RequestMapping(value = "/{docRef}", method = RequestMethod.GET)
    public List<Document> getAllDocuments(@PathVariable int docRef) {
        return documentDAO.getDocuments(docRef);
    }

    @RequestMapping(value = "/store/{storeId}", method = RequestMethod.GET)
    public List<Document> getDocumentsInStore(@PathVariable int storeId) {
        return documentDAO.getDocumentsInStore(storeId);
    }

    //@RequestMapping(method = RequestMethod.POST)
    //public int createDocument() {
      //
       // return documentDAO.createDocument();
   // }
}

