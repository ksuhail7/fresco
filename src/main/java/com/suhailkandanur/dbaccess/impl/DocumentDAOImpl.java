package com.suhailkandanur.dbaccess.impl;

import com.suhailkandanur.dbaccess.DocumentDAO;
import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.DocumentVersion;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class DocumentDAOImpl implements DocumentDAO {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDAOImpl.class);

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertDocumentJdbc;


    @Autowired
    private GenIdDAOImpl genIdDAO;

    @Autowired
    public void setDataSource(final DataSource dataSource) throws Exception {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertDocumentJdbc = new SimpleJdbcInsert(dataSource).withTableName("document");

    }

    @Override
    public boolean exists(int storeId, String docId) {
        return getDocument(storeId, docId) != null;
    }

    @Override
    public List<Document> getDocumentList(int storeId, boolean includeInActive) {
        final List<Document> documentList = this.jdbcTemplate.query(QUERY_DOC_BY_STOREID, new Object[]{storeId}, docRowMapper);
        if(!includeInActive) {
            return documentList.stream().filter(document -> document.isActive()).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        }
        return documentList;
    }

    @Override
    public Document getDocument(int storeId, String docId) {
        try {
            return this.jdbcTemplate.queryForObject(QUERY_DOC_BY_STOREID_AND_DOCID, new Object[]{storeId, docId}, docRowMapper);
        } catch(DataAccessException dae) {
            logger.error("unable to find document with docid {} inside store '{}'", docId, storeId);
            return null;
        }
    }

    @Override
    public Document createDocument(int storeId, String docId, String docIdSha1, String requester) {
        Objects.requireNonNull(requester);
        Objects.requireNonNull(docId);
        Objects.requireNonNull(docIdSha1);
        if(exists(storeId, docId)) {
            throw new IllegalStateException("document with id '" + docId + "' already defined for store '" + storeId + "'");
        }
        int docRef = genIdDAO.generateId("doc_ref");
        Date now = new Date();
        Document document = new Document(docRef, storeId, docId, docIdSha1, requester, now, requester, now, true);
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("docref", docRef)
                .addValue("storeid", storeId)
                .addValue("docid", docId)
                .addValue("docid_sha1", docIdSha1)
                .addValue("is_active", true)
                .addValue("creation_date", now)
                .addValue("created_by", requester)
                .addValue("update_date", now)
                .addValue("updated_by", requester);
        final int noOfRows = this.insertDocumentJdbc.execute(source);
        if(noOfRows < 1) {
            logger.error("unable to insert record for docid {} into document table", docId);
            return null;
        }
        logger.info("document with docid '{}' successfully inserted into db, no. of rows affected {}", docId, noOfRows);
        return document;
    }
}
