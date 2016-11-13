package com.suhailkandanur.entity;

import java.util.*;

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
    private boolean isActive;

    private PriorityQueue<DocumentVersion> documentVersions;


    public Document(int doc_ref, int storeId, String docid, String docid_sha1, String created_by, Date creation_time, String updated_by, Date update_time, boolean isActive) {
        this.docRef = doc_ref;
        this.storeId = storeId;
        this.docId = docid;
        this.docIdSha1 = docid_sha1;
        this.createdBy = created_by;
        this.creationTime = creation_time;
        this.updatedBy = updated_by;
        this.updateTime = update_time;
        this.isActive = isActive;
        this.documentVersions = new PriorityQueue<DocumentVersion>(Collections.reverseOrder((DocumentVersion o1, DocumentVersion o2) -> {
            return Double.compare(o1.getVersion(), o2.getVersion());
        }));

    }

    public boolean isActive() {
        return isActive;
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

    public DocumentVersion getLatestVersion() {
        return documentVersions.peek();
    }

    public List<DocumentVersion> getDocumentVersions() {
        return Collections.unmodifiableList(Arrays.asList(documentVersions.toArray(new DocumentVersion[0])));
    }

    public Document addVersion(DocumentVersion docVersion) {
        this.documentVersions.add(docVersion);
        return this;
    }

    public Document addVersions(List<DocumentVersion> docVerList) {
        this.documentVersions.addAll(docVerList);
        return this;
    }

    public Document addVersion(DocumentVersion[] docVerArray) {
        return addVersions(Arrays.asList(docVerArray));
    }
}
