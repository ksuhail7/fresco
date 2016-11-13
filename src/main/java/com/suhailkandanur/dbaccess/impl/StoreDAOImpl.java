package com.suhailkandanur.dbaccess.impl;

import com.suhailkandanur.dbaccess.RepositoryDAO;
import com.suhailkandanur.dbaccess.StoreDAO;
import com.suhailkandanur.entity.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class StoreDAOImpl implements StoreDAO {

    private static final Logger logger = LoggerFactory.getLogger(StoreDAOImpl.class);

    private SimpleJdbcInsert jdbcInsert;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GenIdDAOImpl genIdDAO;

    @Autowired
    private RepositoryDAO repositoryDAO;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("store");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Store> getAllStores(boolean includeInactive) {
        final List<Store> storeList = this.jdbcTemplate.query(QUERY_STORE_BASE, storeRowMapper);
        if(!includeInactive) //filter active stores
            return storeList.stream().filter(store -> store.isActive()).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        return storeList;
    }

    @Override
    public Store getStore(int storeId) {
        return getStoreGeneric(QUERY_STORE_BY_ID, new Object[]{storeId});

    }

    @Override
    public Store getStore(String name) {
        return getStoreGeneric(QUERY_STORE_BY_NAME, new Object[] {name});
    }

    private Store getStoreGeneric(String sql, Object[] args) {
        final List<Store> storeList = this.jdbcTemplate.query(sql, args, storeRowMapper);
        return (storeList != null && storeList.size() == 1) ? storeList.get(0) : null;

    }

    @Override
    public Store createStore(int repoId, String name, String description, String requester) {
        Objects.requireNonNull(requester);
        Objects.requireNonNull(name);
        if(getStore(name) != null)
            throw new IllegalStateException("store already exists with name '" + name + "'");
        if(repositoryDAO.getRepository(repoId) == null) {
            throw new IllegalStateException("repository id '" + repoId + "' does not exist");
        }
        int storeId = genIdDAO.generateId("store_id");
        Date now = new Date();
        Store store = new Store(storeId, repoId, name, description, true, requester, now, requester, now);
        return createStore(store);
    }

    private Store createStore(final Store store) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", store.getStoreId())
                .addValue("name", store.getName())
                .addValue("repo_id", store.getRepositoryId())
                .addValue("creation_date", store.getCreationTime())
                .addValue("created_by", store.getCreatedBy())
                .addValue("update_date", store.getUpdateTime())
                .addValue("updated_by", store.getUpdatedBy())
                .addValue("is_active", store.isActive());
        try {
            int rowsInserted = this.jdbcInsert.execute(parameters);
            if (rowsInserted == 1) {
                logger.info("store with id '{}' created, no. of rows inserted {}", store.getStoreId(), rowsInserted);
                return store;
            }
        } catch(DataAccessException dae) {
            logger.error("unable to create store with id '{}', error: {}", store.getStoreId(), dae.getMessage());
        }
        return null;
    }
}
