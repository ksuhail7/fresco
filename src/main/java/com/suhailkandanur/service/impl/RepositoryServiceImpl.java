package com.suhailkandanur.service.impl;

import com.suhailkandanur.dbaccess.FileSystemDAO;
import com.suhailkandanur.dbaccess.RepoFileSystemMappingDAO;
import com.suhailkandanur.dbaccess.RepositoryDAO;
import com.suhailkandanur.entity.FileSystem;
import com.suhailkandanur.entity.Repo;
import com.suhailkandanur.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    private FileSystemDAO fileSystemDAO;

    @Autowired
    private RepoFileSystemMappingDAO repoFileSystemMappingDAO;

    @Override
    public void afterPropertiesSet() {
        //initialize();
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
                            .map(fs -> Paths.get(fs.getPath(), "fresco", "repo_" + repo.getRepositoryId()))
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

    @Override
    @Transactional
    public Repo createRepository(final String name, final String description, final String requester, final String[] fileSystems) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(requester);
        if (repositoryDAO.getRepository(name) != null)
            throw new RuntimeException("repository already exists with name " + name);
        final List<FileSystem> fileSystemObjects = Arrays.asList(fileSystems).stream().map(path ->
                fileSystemDAO.getOrCreateFileSystem(path, requester)
        ).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        final Repo repo = repositoryDAO.createRepository(name, description, requester);
        //add mapping to repo and filesystems
        int repoId = repo.getRepositoryId();
        fileSystemObjects.stream()
                .filter(fileSystem -> !repoFileSystemMappingDAO.isRepoMappedToFileSystem(repoId, fileSystem.getFilesystemId()))
                .forEach(fileSystem -> repoFileSystemMappingDAO.createMapping(repoId, fileSystem.getFilesystemId(), requester));
        repo.addFileSystems(getMappedFileSystems(repo));
        return repo;
    }

    @Override
    public List<Repo> getAllRepositories(boolean includeInActive) {
        return repositoryDAO.getAllRepositories(includeInActive).stream()
                .map(repo -> {
                    repo.addFileSystems(getMappedFileSystems(repo));
                    return repo;
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public Repo getRepository(int id) {
        final Repo repository = repositoryDAO.getRepository(id);
        if(repository != null)
            repository.addFileSystems(getMappedFileSystems(repository));
        return repository;
    }

    private List<FileSystem> getMappedFileSystems(Repo repo) {
        return (repo != null) ? repoFileSystemMappingDAO.getMappedFileSystems(repo.getRepositoryId()).stream()
                .filter(repoFsMapping -> repoFsMapping.isActive())
                .map(repoFsMapping -> fileSystemDAO.getFileSystem(repoFsMapping.getFsId()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList))
                : Collections.emptyList();
    }
}
