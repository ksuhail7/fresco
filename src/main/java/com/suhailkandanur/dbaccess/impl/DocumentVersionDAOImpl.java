package com.suhailkandanur.dbaccess.impl;

import com.suhailkandanur.dbaccess.DocumentVersionDAO;
import com.suhailkandanur.entity.DocumentVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by suhail on 2016-11-12.
 */
@Repository
public class DocumentVersionDAOImpl implements DocumentVersionDAO {
    private static final Logger logger = LoggerFactory.getLogger(DocumentVersionDAOImpl.class);


    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("document_version");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<DocumentVersion> getDocumentVersions(int docRef) {
        return this.jdbcTemplate.query(QUERY_DOCVER_BY_DOCREF, new Object[]{docRef}, documentVersionRowMapper);
    }

    @Override
    public DocumentVersion getDocumentVersion(int docRef, long version) {
        final List<DocumentVersion> docVerList = this.jdbcTemplate.query(QUERY_DOCVER_BY_DOCREF_VERSION, new Object[]{docRef, version}, documentVersionRowMapper);
        if (docVerList != null && docVerList.size() > 1)
            throw new IllegalStateException("multiple entries found for the same docref '" + docRef + "' and version '" + version + "'");
        return (docVerList != null && docVerList.size() == 1) ? docVerList.get(0) : null;
    }

    @Override
    public DocumentVersion createDocumentVersion(int docRef, long version, String filename, long fileSize, String mimeType, String sha1Cksum, String requester) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(sha1Cksum);
        Objects.requireNonNull(requester);
        Date now = new Date();
        DocumentVersion documentVersion = new DocumentVersion(docRef, version, filename, fileSize, mimeType, sha1Cksum, now, requester, now, requester, true);
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("docref", docRef)
                .addValue("version", version)
                .addValue("filename", filename)
                .addValue("filesize_in_bytes", fileSize)
                .addValue("mimetype", mimeType)
                .addValue("sha1_checksum", sha1Cksum)
                .addValue("creation_date", now)
                .addValue("created_by", requester)
                .addValue("update_date", now)
                .addValue("updated_by", requester)
                .addValue("is_active", true);
        final int noOfRows = this.jdbcInsert.execute(source);
        if(noOfRows < 1) {
            logger.error("unable to insert record into document_version table");
            return null;
        }
        logger.info("document version created, no. of records inserted {}", noOfRows);
        return documentVersion;
    }
}
