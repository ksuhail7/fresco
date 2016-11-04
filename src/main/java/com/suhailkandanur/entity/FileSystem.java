package com.suhailkandanur.entity;

import java.util.Date;

/**
 * Created by suhail on 2016-11-03.
 */
public class FileSystem {
    private int filesystemId;
    private String path;
    private boolean isActive;
    private String createdBy;
    private Date creationTime;
    private String updatedBy;
    private Date updateTime;

    public FileSystem(int filesystemId, String path, boolean isActive, String createdBy, Date creationTime, String updatedBy, Date updateTime) {
        this.filesystemId = filesystemId;
        this.path = path;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
    }

    public int getFilesystemId() {
        return filesystemId;
    }

    public String getPath() {
        return path;
    }

    public boolean isActive() {
        return isActive;
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