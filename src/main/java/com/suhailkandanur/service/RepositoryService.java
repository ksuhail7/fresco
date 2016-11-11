package com.suhailkandanur.service;

import com.suhailkandanur.entity.Repo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by suhail on 2016-11-08.
 */
@Component
public interface RepositoryService {
    int createRepository(String name, String description, String requester, String[] fileSystems);

    default List<Repo> getAllRepositories() {
        return getAllRepositories(false);
    }

    List<Repo> getAllRepositories(boolean includeInActive);

    Repo getRepository(int id);
}
