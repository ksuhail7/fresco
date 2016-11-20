package com.suhailkandanur.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suhailkandanur.dbaccess.RepositoryDAO;
import com.suhailkandanur.dbaccess.StoreDAO;
import com.suhailkandanur.entity.Store;
import com.suhailkandanur.messaging.Publisher;
import com.suhailkandanur.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Created by suhail on 2016-11-08.
 */
@Service
public class StoreServiceImpl implements StoreService {
    private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    private List<Store> activeStores;


    @Autowired
    private RepositoryDAO repositoryDAO;

    @Autowired
    private StoreDAO storeDAO;

    @Autowired
    private Publisher publisher;

    @Override
    @Transactional
    public Store createStore(String name, String description, int repoId, String requester) {
        final Store store = storeDAO.createStore(repoId, name, description, requester);
        try {
            publisher.publishBroadcast("fresco", new ObjectMapper().writeValueAsBytes(store));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return store;
    }

    @Override
    public List<Store> getAllStores(boolean includeInactive) {
        return storeDAO.getAllStores(includeInactive);
    }

    @Override
    public Store getStore(int storeId) {
        return storeDAO.getStore(storeId);
    }

    @Override
    public Store getStore(String name) {
        return storeDAO.getStore(name);
    }
}
