package com.suhailkandanur.service.impl;

import com.suhailkandanur.dbaccess.RepositoryDAO;
import com.suhailkandanur.dbaccess.StoreDAO;
import com.suhailkandanur.dbaccess.impl.RepositoryDAOImpl;
import com.suhailkandanur.dbaccess.impl.StoreDAOImpl;
import com.suhailkandanur.entity.Store;
import com.suhailkandanur.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by suhail on 2016-11-08.
 */
@Service
public class StoreServiceImpl implements InitializingBean, StoreService {
    private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    private List<Store> activeStores;


    @Autowired
    private RepositoryDAO repositoryDAO;

    @Autowired
    private StoreDAO storeDAO;

    @Override
    public void afterPropertiesSet() throws Exception {
        //initialize();
    }

    // protected void initialize() {
    //     logger.info("initializing store service");
    //     activeStores = storeDAO.getAllStores(true);
    //     //activeStores.
    //
    // }

    @Override
    public int createStore(String name, String description, int repoId, String requester) {
        return 0;
    }

    @Override
    public List<Store> getAllStores(boolean includeInactive) {
        return null;
    }

    @Override
    public Store getStore(int storeId) {
        return null;
    }
}
