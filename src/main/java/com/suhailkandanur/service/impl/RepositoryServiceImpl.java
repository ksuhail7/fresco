package com.suhailkandanur.service.impl;

import com.suhailkandanur.dbaccess.RepositoryDAO;
import com.suhailkandanur.entity.FileSystem;
import com.suhailkandanur.entity.Repo;
import com.suhailkandanur.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

/**
 * Created by suhail on 2016-11-08.
 */
@Component
public class RepositoryServiceImpl implements RepositoryService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    private List<Repo> activeRepositories;
    private Map<Integer, FileSystem> validFileSystems;

    @Autowired
    private RepositoryDAO repositoryDAO;


    @Override
    public void afterPropertiesSet() {
        initialize();
    }

    protected void initialize() {
        //get all repositories configured in the database
        logger.info("initializing repositories");
        activeRepositories = repositoryDAO.getAllRepositories(true);

        validFileSystems = activeRepositories.parallelStream()
                .map(repo -> repo.getFileSystemSet())
                .flatMap(set -> set.stream())
                .filter(fs -> Files.exists(Paths.get(fs.getPath())))
                .collect(Collectors.toMap(fs -> fs.getFilesystemId(), Function.identity()));




        activeRepositories.parallelStream()
                .forEach(repo -> {
                    repo.getFileSystemSet().parallelStream()
                            .filter(fs -> validFileSystems.containsKey(fs.getFilesystemId()))
                            .map(fs -> Paths.get(fs.getPath(), "fresco", "repo_"  + repo.getRepositoryId()))
                            .filter(Files::notExists)
                            .forEach(path -> {
                                    try {
                                        logger.debug("creating directory {}", path);
                                        Files.createDirectories(path);
                                    } catch (IOException e) {
                                        logger.error("unable to create directory: {}", path);
                                        e.printStackTrace();
                                    }
                            });
                });


    }

    public void createRepository() {
        throw new NotImplementedException();
    }
}
