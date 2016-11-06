package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.DocumentVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class DocumentDAO implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDAO.class);

    private static final RowMapper<Document> docRowMapper = (rs, rowNum) -> {
        Document doc = new Document(rs.getInt("id"),
                rs.getInt("storeid")
                rs.getString("docid"), rs.getString("docid_sha1"),
                rs.getString("created_by"),
                rs.getDate("creation_date"),
                rs.getString("updated_by"),
                rs.getDate("update_date"));
        return doc;
    };
    private SimpleJdbcInsert insertJdbcCall;
    private JdbcTemplate  jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenIdDAO genIdDAO;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.insertJdbcCall = new SimpleJdbcInsert(dataSource).withTableName("document");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<DocumentVersion> getDocuments() {
        return Collections.emptyList();
    }

    public DocumentVersion getDocument(String docid) {
        return null;
    }

    public void createDocument(DocumentVersion documentVersion) {

    }

}
