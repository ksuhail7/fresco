package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.Repo;

import java.util.List;

/**
 * Created by suhail on 2016-11-10.
 */
public interface RepositoryDAO {
    List<Repo> getAllRepositories(boolean includeInactive);

    default List<Repo> getAllRepositories() {
        return getAllRepositories(false);
    }
}
