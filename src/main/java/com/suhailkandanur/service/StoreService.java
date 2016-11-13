package com.suhailkandanur.service;

import com.suhailkandanur.entity.Store;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by suhail on 2016-11-08.
 */
@Service
public interface StoreService {
    Store createStore(String name, String description, int repoId, String requester);

    default List<Store> getAllStores() {
        return getAllStores(false);
    }

    List<Store> getAllStores(boolean includeInactive);

    Store getStore(int storeId);

    Store getStore(String name);
}
