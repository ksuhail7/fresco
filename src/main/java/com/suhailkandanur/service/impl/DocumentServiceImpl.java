package com.suhailkandanur.service.impl;

import com.suhailkandanur.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Created by suhail on 2016-11-08.
 */
@Service
public class DocumentServiceImpl implements DocumentService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    @Override
    public void createDocument() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
