package com.suhailkandanur.util;

/**
 * Created by suhail on 2016-11-03.
 */
public interface Lock {
    boolean isLocked();
    void lock() throws Exception;
    boolean tryLock();
    void release();
}
