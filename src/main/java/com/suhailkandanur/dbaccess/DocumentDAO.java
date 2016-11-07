package com.suhailkandanur.dbaccess;

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
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.bouncycastle.asn1.ua.DSTU4145NamedCurves.params;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class DocumentDAO implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDAO.class);

    private static final String SELECT_DOC_QUERY_BASE = "SELECT docref, storeid, docid, docid_sha1, created_by, creation_date, updated_by, update_date, is_active " +
            " FROM document ";
    private static final String SELECT_DOC_BY_DOCREF = SELECT_DOC_QUERY_BASE + " WHERE storeid = ? and docref = ?";
    private static final String SELECT_DOC_BY_DOCID = SELECT_DOC_QUERY_BASE + " WHERE storeid = ? and docid = ?";
    private static final String SELECT_DOC_BY_STOREID = SELECT_DOC_QUERY_BASE + " WHERE storeid = ?";

    private static final String SELECT_DOC_VERSION_QUERY_BASE = "SELECT docref, version, filename, filesize_in_bytes, mimetype, sha1_checksum, creation_date, created_by, update_date, updated_by, is_active " +
            " FROM document_version";
    private static final String SELECT_DOC_VERSION_BY_DOCREF = SELECT_DOC_VERSION_QUERY_BASE + " WHERE docref = ?";

    private static final String SELECT_DOC_VERSION_BY_DOCREF_AND_VERSION = SELECT_DOC_VERSION_BY_DOCREF + " and version = ?";


    private static final RowMapper<Document> docRowMapper = (rs, rowNum) -> {
        return new Document(rs.getInt("docref"),
                rs.getInt("storeid"),
                rs.getString("docid"),
                rs.getString("docid_sha1"),
                rs.getString("created_by"),
                rs.getDate("creation_date"),
                rs.getString("updated_by"),
                rs.getDate("update_date"),
                rs.getBoolean("is_active"));
    };

    private static final RowMapper<DocumentVersion> docVersionRowMapper = (rs, rowNum) -> {
        return new DocumentVersion(rs.getInt("docref"),
                rs.getLong("version"),
                rs.getString("filename"),
                rs.getLong("filesize_in_bytes"),
                rs.getString("mimetype"),
                rs.getString("sha1_checksum"),
                rs.getDate("creation_date"),
                rs.getString("created_by"),
                rs.getDate("update_date"),
                rs.getString("updated_by"),
                rs.getBoolean("is_active")
        );
    };

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertDocumentJdbc;
    private SimpleJdbcInsert insertDocumentVersionJdbc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenIdDAO genIdDAO;


    /**
     * IN docid varchar(128),
     * IN storeid int,
     * IN docid_sha1 varchar(40),
     * IN version long,
     * IN filename varchar(128),
     * IN filesize int,
     * IN mimetype varchar(128),
     * IN sha1cksum varchar(40),
     * IN requestor varchar(64),
     * IN is_active boolean,
     * OUT docref int
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertDocumentJdbc = new SimpleJdbcInsert(dataSource).withTableName("document");
        this.insertDocumentVersionJdbc = new SimpleJdbcInsert(dataSource).withTableName("document_version");

    }


    private List<Document> getDocumentsByQuery(String query, Object... args) {
        return Optional.ofNullable(this.jdbcTemplate.query(query, args, docRowMapper)).orElse(Collections.emptyList()).parallelStream().map(document -> {
            document.addVersions(getDocumentVersions(document.getDocId()));
            return document;
        }).collect(Collectors.toList());
    }

    public List<Document> getAllDocumentsInStore(int storeId) {
        return Collections.unmodifiableList(getDocumentsByQuery(SELECT_DOC_BY_STOREID, storeId));
    }

    public List<Document> getDocumentsFromStore(int storeId, String docId) {
        return Collections.unmodifiableList(getDocumentsByQuery(SELECT_DOC_BY_DOCID, storeId, docId));

    }

    public List<Document> getDocumentsFromStore(int storeId, int docRef) {
        return Collections.unmodifiableList(getDocumentsByQuery(SELECT_DOC_BY_DOCREF, storeId, docRef));
    }

    public Optional<DocumentVersion> getDocumentVersion(int docRef, long version) {
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(SELECT_DOC_VERSION_BY_DOCREF_AND_VERSION, new Object[]{docRef, version}, docVersionRowMapper));
        } catch (DataAccessException dae) {
            return Optional.empty();
        }
    }

    private List<DocumentVersion> getDocumentVersions(String docRef) {
        return Optional.ofNullable(this.jdbcTemplate.query(SELECT_DOC_VERSION_BY_DOCREF, new Object[]{docRef}, docVersionRowMapper))
                .map(Collections::unmodifiableList)
                .orElse(Collections.emptyList());
    }

    private Optional<Integer> getDocRefForDocId(int storeId, String docId) {
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject("select docref from document where storeid = ? and docid = ?", new Object[]{storeId, docId}, Integer.class));
        } catch (DataAccessException dae) {
            return Optional.empty();
        }
    }


    @Transactional
    public int createDocument(String docId, int storeId, String docIdSha1, long version, String filename, long filesize, String mimetype, String sha1Cksum, String requestor, boolean isActive) {
        Date createDate = new Date();
        //Map<String, Object> out = createDocumentJdbcCall.execute(params);
        Optional<Integer> docRefOptional = getDocRefForDocId(storeId, docId);
        int docRef = docRefOptional.orElse(-1);
        if (docRef == -1) {
            docRef = genIdDAO.generateId("document_ref");
            //insert into document object
            MapSqlParameterSource docParams = new MapSqlParameterSource().addValue("docref", docRef)
                    .addValue("docid", docId)
                    .addValue("storeid", storeId)
                    .addValue("docid_sha1", docIdSha1)
                    .addValue("created_by", requestor)
                    .addValue("creation_date", createDate)
                    .addValue("update_date", createDate)
                    .addValue("updated_by", requestor)
                    .addValue("is_active", isActive);
            int noRows = insertDocumentJdbc.execute(docParams);
            logger.info("record inserted into document table, no. of rows affected {}", noRows);
        }
        try {
            //document already present, insert into document_version
            if (getDocumentVersion(docRef, version) != null) {
                SqlParameterSource params = new MapSqlParameterSource()
                        .addValue("docref", docRef)
                        .addValue("version", version)
                        .addValue("filename", filename)
                        .addValue("filesize", filesize)
                        .addValue("mimetype", mimetype)
                        .addValue("sha1cksum", sha1Cksum)
                       // .addValue("creation_date", createDate)
                        .addValue("created_by", requestor)
                        .addValue("update_date", createDate)
                        .addValue("updated_by", createDate)
                        .addValue("is_active", isActive);

                int noRows = insertDocumentVersionJdbc.execute(params);
                logger.info("record inserted into document_version table, no. of rows affected {}", noRows);
                return docRef;
            } else {
                logger.error("document version with docref '{}' and version '{}' already exists", docRef, version);
                return -1;
            }
        } catch(DataAccessException dae) {
            logger.error("error creating record in document_version table, error: {}", dae.getMessage());
            return -1;
        }
    }

}
