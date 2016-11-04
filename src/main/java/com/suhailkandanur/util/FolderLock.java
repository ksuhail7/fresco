package com.suhailkandanur.util;

/**
 * Created by suhail on 2016-11-03.
 */
public class FolderLock implements Lock {
    private final String folderLocation;

    private volatile boolean isLocked;

    public FolderLock(String folderLocation) {
        this.folderLocation = folderLocation;
    }
    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void lock() {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public void release() {

    }
}
