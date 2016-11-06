package com.suhailkandanur.entity;

import java.util.Date;

/**
 * Created by suhail on 2016-11-03.
 */
public class Repo {
    private int repositoryId;
    private String name;
    private String description;
    private boolean isActive;
    private String createdBy;
    private Date creationTime;
    private String updatedBy;
    private Date updateTime;

    public Repo(int repositoryId, String name, String description, boolean isActive, String createdBy, Date creationTime, String updatedBy, Date updateTime) {
        this.repositoryId = repositoryId;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
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
