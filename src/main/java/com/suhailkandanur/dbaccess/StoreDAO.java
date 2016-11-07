package com.suhailkandanur.dbaccess;

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
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

import static org.bouncycastle.asn1.ua.DSTU4145NamedCurves.params;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class StoreDAO implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(StoreDAO.class);

    private static final RowMapper<Store> storeRowMapper = (rs, rowNum) -> {
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

    private SimpleJdbcInsert jdbcInsert;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenIdDAO genIdDAO;

    @Override
    public void afterPropertiesSet() {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("store");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Store> getAllStores() {
        return this.jdbcTemplate.query("select * from store", storeRowMapper);
    }

    public Store getStore(int storeId) {
        return this.jdbcTemplate.queryForObject("select * from store where id = ? ", new Object[] {storeId}, storeRowMapper);

    }

    public int createStore(Store store) {
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
                return store.getStoreId();
            }
        } catch(DataAccessException dae) {
            logger.error("unable to create store with id '{}', error: {}", store.getStoreId(), dae.getMessage());
        }
        return -1;
    }

    public int createStore(String storeName, String description, int repoId, String requester) {
        Date now = new Date();
        Store store = new Store(genIdDAO.generateId("store_id"), repoId, storeName, description, true, requester, now, requester, now);
        return createStore(store);
    }

   // public boolean updateStore(Store store) {
     //   return false;
    //}
}
