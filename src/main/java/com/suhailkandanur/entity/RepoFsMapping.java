package com.suhailkandanur.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by suhail on 2016-11-11.
 */
public class RepoFsMapping implements Serializable {
    private int repoId;
    private int fsId;
    private boolean isActive;
    private String createdBy;
    private Date creationDate;
    private String updatedBy;
    private Date updateDate;

    public RepoFsMapping(int repoId, int fsId, boolean isActive, String createdBy, Date creationDate, String updatedBy, Date updateDate) {
        this.repoId = repoId;
        this.fsId = fsId;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.updatedBy = updatedBy;
        this.updateDate = updateDate;
    }

    public int getRepoId() {
        return repoId;
    }

    public int getFsId() {
        return fsId;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
}
