package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.Document;
import com.suhailkandanur.entity.DocumentVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class DocumentDAO implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDAO.class);

    private static final String SELECT_DOC_QUERY_BASE = "SELECT docref, storeid, docid, docid_sha1, created_by, creation_date, updated_by, update_date, is_active " +
            " FROM document ";
    private static final String SELECT_DOC_BY_DOCREF = SELECT_DOC_QUERY_BASE + " WHERE docref = ?";
    private static final String SELECT_DOC_BY_DOCID = SELECT_DOC_QUERY_BASE + " WHERE docid = ?";
    private static final String SELECT_DOC_BY_STOREID = SELECT_DOC_QUERY_BASE + " WHERE storeid = ?";

    private static final String SELECT_DOC_VERSION_QUERY_BASE = "SELECT docref, version, filename, filesize_in_bytes, mimetype, sha1_checksum, creation_date, created_by, update_date, updated_by, is_active " +
            " FROM document_version";
    private static final String SELECT_DOC_VERSION_BY_DOCREF = SELECT_DOC_VERSION_QUERY_BASE + " WHERE docref = ?";


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
    private SimpleJdbcCall createDocumentJdbcCall;

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
        this.createDocumentJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("create_document")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("docid", "storeid", "docid_sha1", "version", "fliename", "filesize", "mimetype", "sha1cksum",
                        "requestor", "is_active")
                .declareParameters(new SqlParameter("docid", Types.VARCHAR),
                        new SqlParameter("storeid", Types.INTEGER),
                        new SqlParameter("docid_sha1", Types.VARCHAR),
                        new SqlParameter("version", Types.BIGINT),
                        new SqlParameter("fliename", Types.VARCHAR),
                        new SqlParameter("filesize", Types.INTEGER),
                        new SqlParameter("mimetype", Types.VARCHAR),
                        new SqlParameter("sha1cksum", Types.VARCHAR),
                        new SqlParameter("requestor", Types.VARCHAR),
                        new SqlParameter("is_active", Types.BOOLEAN),
                        new SqlOutParameter("docref", Types.INTEGER));
    }


    private List<Document> getDocumentsByQuery(String query, Object... args) {
        return Optional.ofNullable(this.jdbcTemplate.query(query, args, docRowMapper)).orElse(Collections.emptyList()).parallelStream().map(document -> {
            document.addVersions(getDocumentVersions(document.getDocId()));
            return document;
        }).collect(Collectors.toList());
    }
    public List<Document> getDocumentsInStore(int storeId) {
        return Collections.unmodifiableList(getDocumentsByQuery(SELECT_DOC_BY_STOREID, storeId));
    }

    public List<Document> getDocuments(String docId) {
        return Collections.unmodifiableList(getDocumentsByQuery(SELECT_DOC_BY_DOCID, docId));

    }

    public List<Document> getDocuments(int docRef) {
        return Collections.unmodifiableList(getDocumentsByQuery(SELECT_DOC_BY_DOCREF, docRef));
    }

    private List<DocumentVersion> getDocumentVersions(String docRef) {
        return Optional.ofNullable(this.jdbcTemplate.query(SELECT_DOC_VERSION_BY_DOCREF, new Object[]{docRef}, docVersionRowMapper))
                .map(Collections::unmodifiableList)
                .orElse(Collections.emptyList());
    }

    public int createDocument(String docId, int storeId, String docIdSha1, long version, String filename, long filesize, String mimetype, String sha1Cksum, String requestor, boolean isActive) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("docid", docId)
                .addValue("storeid", storeId)
                .addValue("docid_sha1", docIdSha1)
                .addValue("version", version)
                .addValue("filename", filename)
                .addValue("filesize", filesize)
                .addValue("mimetype", mimetype)
                .addValue("sha1cksum", sha1Cksum)
                .addValue("requestor", requestor)
                .addValue("is_active", isActive);
        Map<String, Object> out = createDocumentJdbcCall.execute(params);
        return (int) out.get("docref");
    }

}
