package com.suhailkandanur.controller;

import com.suhailkandanur.entity.Repo;
import com.suhailkandanur.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by suhail on 2016-11-04.
 */
@RequestMapping("/fresco/repository")
@RestController
public class FrescoRepositoryController {

    private static Logger logger = LoggerFactory.getLogger(FrescoRepositoryController.class);

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Repo> getAllRepositories() {
        return repositoryService.getAllRepositories();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Repo getRepository(@PathVariable int id) {
        return repositoryService.getRepository(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public int createRepository(String name, String description, String requester, String[] fileSystems) {
        return repositoryService.createRepository(name, description, requester, fileSystems);
    }
}

