package com.suhailkandanur.entity;

import java.util.Date;

/**
 * Created by suhail on 2016-11-03.
 */
public class DocumentVersion {
    private int docRef;
    private String fileName;
    private long fileSize;
    private long version;
    private String sha1Cksum;
    private String createdBy;
    private Date creationTime;
    private String updatedBy;
    private Date updateTime;

    public DocumentVersion(int docRef, String fileName, long fileSize, long version, String sha1Cksum, String createdBy, Date creationTime, String updatedBy, Date updateTime) {
        this.docRef = docRef;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.version = version;
        this.sha1Cksum = sha1Cksum;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
    }

    public int getDocRef() {
        return docRef;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getVersion() {
        return version;
    }

    public String getSha1Cksum() {
        return sha1Cksum;
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
}
