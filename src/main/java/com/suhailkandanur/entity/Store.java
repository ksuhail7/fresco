package com.suhailkandanur.entity;

import java.util.Date;

/**
 * Created by suhail on 2016-11-03.
 */
public class Store {
    private int storeId;
    private int repositoryId;
    private String name;
    private String description;
    private String createdBy;
    private Date creationTime;
    private String updatedBy;
    private Date updateTime;
    private boolean isActive;

    public Store(int storeId, int repositoryId, String name, String description, boolean isActive, String createdBy, Date creationTime, String updatedBy, Date updateTime) {
        this.storeId = storeId;
        this.repositoryId = repositoryId;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getRepositoryId() {
        return repositoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public boolean isActive() {
        return isActive;
    }
}
