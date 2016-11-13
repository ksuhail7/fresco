package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.Document;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by suhail on 2016-11-10.
 */
public interface DocumentDAO {
    RowMapper<Document> docRowMapper = (rs, rowNum) ->
            new Document(rs.getInt("docref"),
                    rs.getInt("storeid"),
                    rs.getString("docid"),
                    rs.getString("docid_sha1"),
                    rs.getString("created_by"),
                    rs.getDate("creation_date"),
                    rs.getString("updated_by"),
                    rs.getDate("update_date"),
                    rs.getBoolean("is_active"));

    String QUERY_DOC_BASE = "select * from document";
    String QUERY_DOC_BY_STOREID = QUERY_DOC_BASE + " where storeid = ?";
    String QUERY_DOC_BY_STOREID_AND_DOCID = QUERY_DOC_BY_STOREID + " and docid = ?";

    boolean exists(int storeId, String docId);

    default boolean notExists(int storeId, String docId) {
        return !exists(storeId, docId);
    }

    List<Document> getDocumentList(int storeId, boolean includeInActive);

    default List<Document> getDocumentList(int storeId) {
        return getDocumentList(storeId, false);
    }

    Document getDocument(int storeId, String docId);

    Document createDocument(int storeId, String docId, String docIdSha1, String requester);


}
