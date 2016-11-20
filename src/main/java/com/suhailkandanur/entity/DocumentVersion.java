package com.suhailkandanur.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by suhail on 2016-11-06.
 */
public class DocumentVersion implements Serializable {
    private int docRef;
    private long version;
    private String fileName;
    private long fileSize;
    private String mimeType;
    private String sha1Cksum;
    private Date creationDate;
    private String createdBy;
    private Date updateDate;
    private String updatedBy;
    private boolean isActive;

    public DocumentVersion(int docRef, long version, String fileName, long fileSize, String mimeType, String sha1Cksum, Date creationDate, String createdBy, Date updateDate, String updatedBy, boolean isActive) {
        this.docRef = docRef;
        this.version = version;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.sha1Cksum = sha1Cksum;
        this.creationDate = creationDate;
        this.createdBy = createdBy;
        this.updateDate = updateDate;
        this.updatedBy = updatedBy;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getDocRef() {
        return docRef;
    }

    public long getVersion() {
        return version;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getSha1Cksum() {
        return sha1Cksum;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
