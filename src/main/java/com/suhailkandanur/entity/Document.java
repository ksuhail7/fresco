package com.suhailkandanur.entity;

import java.util.Date;

/**
 * Created by suhail on 2016-11-03.
 */
public class Document {
    private int docRef;
    private int storeId;
    private String docId;
    private String docIdSha1;
    private String createdBy;
    private Date creationTime;
    private String updatedBy;
    private Date updateTime;

    public Document(int doc_ref, int storeId, String docid, String docid_sha1, String created_by, Date creation_time, String updated_by, Date update_time) {
        this.docRef = doc_ref;
        this.storeId = storeId;
        this.docId = docid;
        this.docIdSha1 = docid_sha1;
        this.createdBy = created_by;
        this.creationTime = creation_time;
        this.updatedBy = updated_by;
        this.updateTime = update_time;
    }

    public int getDocRef() {
        return docRef;
    }

    public String getDocId() {
        return docId;
    }

    public String getDocIdSha1() {
        return docIdSha1;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public int getStoreId() {
        return storeId;
    }
}
