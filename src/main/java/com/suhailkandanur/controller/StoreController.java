package com.suhailkandanur.controller;

import com.suhailkandanur.dbaccess.StoreDAO;
import com.suhailkandanur.entity.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;
import java.util.List;
import java.util.Optional;

/**
 * Created by suhail on 2016-11-07.
 */
@RestController
@RequestMapping("/fresco/store")
public class StoreController {

    @Autowired
    private StoreDAO storeDAO;

    @RequestMapping(method = RequestMethod.GET)
    public List<Store> getAllStores() {
        return storeDAO.getAllStores();
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    public Store getStore(@PathVariable int storeId) {
        return storeDAO.getStore(storeId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public int createStore(String name, int repoId, String requester) {
        return storeDAO.createStore(name, null, repoId, Optional.ofNullable(requester).orElse(System.getProperty("user.name")));
    }
}
