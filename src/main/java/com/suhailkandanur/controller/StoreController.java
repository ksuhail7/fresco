package com.suhailkandanur.controller;

import com.suhailkandanur.entity.Store;
import com.suhailkandanur.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.name;

/**
 * Created by suhail on 2016-11-07.
 */
@RestController
@RequestMapping("/fresco/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    public Store getStore(@PathVariable int storeId) {
        return storeService.getStore(storeId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Store createStore(int repoId, String name, String description, String requester) {
        requester = Optional.ofNullable(requester).orElse(System.getProperty("user.name"));
        return storeService.createStore(name, description, repoId, requester);
    }
}
