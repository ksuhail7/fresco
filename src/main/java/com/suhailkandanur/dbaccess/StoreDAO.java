package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.Store;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by suhail on 2016-11-10.
 */
public interface StoreDAO {
    String QUERY_STORE_BASE = "select * from store";
    String QUERY_STORE_BY_ID = QUERY_STORE_BASE + " where id = ?";
    String QUERY_STORE_BY_NAME = QUERY_STORE_BASE + " where name = ?";

    RowMapper<Store> storeRowMapper = (rs, rowNum) -> {
        Store store = new Store(rs.getInt("id"),
                rs.getInt("repo_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("is_active"),
                rs.getString("created_by"),
                rs.getDate("creation_date"),
                rs.getString("updated_by"),
                rs.getDate("update_date"));
        return store;
    };
    List<Store> getAllStores(boolean includeInactive);
    Store getStore(int storeId);
    Store getStore(String name);
    Store createStore(int repoId, String name, String description, String requester);
    default boolean exists(int storeId) { return getStore(storeId) != null; }
    default boolean exists(String storeName) { return getStore(storeName) != null; }
}
