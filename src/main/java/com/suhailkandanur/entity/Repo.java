package com.suhailkandanur.entity;

import java.util.*;

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
    private Set<FileSystem> fileSystemSet;

    public Repo(int repositoryId, String name, String description, boolean isActive, String createdBy, Date creationTime, String updatedBy, Date updateTime) {
        this.repositoryId = repositoryId;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
        this.fileSystemSet = new HashSet<>();
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

    public Set<FileSystem> getFileSystemSet() {
        return Optional.ofNullable(fileSystemSet).map(Collections::unmodifiableSet).orElse(Collections.emptySet());
    }

    public boolean addFileSystem(FileSystem fileSystem) {
        return this.fileSystemSet.add(fileSystem);
    }

    public boolean addFileSystems(List<FileSystem> fileSystems, boolean clean) {
        if(clean) this.fileSystemSet.clear();
        return this.fileSystemSet.addAll(fileSystems);
    }

    public boolean addFileSystems(List<FileSystem> fileSystems) {
        return addFileSystems(fileSystems, false);
    }
}
