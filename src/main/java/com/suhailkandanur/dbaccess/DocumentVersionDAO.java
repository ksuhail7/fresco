package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.DocumentVersion;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by suhail on 2016-11-12.
 */
public interface DocumentVersionDAO {
    RowMapper<DocumentVersion> documentVersionRowMapper = (rs, rowNum) ->
            new DocumentVersion(rs.getInt("docref"),
                    rs.getLong("version"),
                    rs.getString("filename"),
                    rs.getLong("filesize_in_bytes"),
                    rs.getString("mimetype"),
                    rs.getString("sha1_checksum"),
                    rs.getDate("creation_date"),
                    rs.getString("created_by"),
                    rs.getDate("update_date"),
                    rs.getString("updated_by"),
                    rs.getBoolean("is_active"));

    String QUERY_DOCVER_BASE = "select * from document_version";
    String QUERY_DOCVER_BY_DOCREF = QUERY_DOCVER_BASE + " where docref = ?";
    String QUERY_DOCVER_BY_DOCREF_VERSION = QUERY_DOCVER_BY_DOCREF + " and version = ?";

    List<DocumentVersion> getDocumentVersions(int docRef);

    DocumentVersion getDocumentVersion(int docRef, long version);

    DocumentVersion createDocumentVersion(int docRef, long version, String filename, long fileSize, String mimeType, String sha1Cksum, String requester);

}
