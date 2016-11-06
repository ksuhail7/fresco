package com.suhailkandanur.controller;

import com.suhailkandanur.dbaccess.RepositoryDAO;
import com.suhailkandanur.entity.Repo;
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
    private RepositoryDAO repositoryDAO;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Repo> getAllRepositories() {
        return repositoryDAO.getAllRepositories();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Repo getRepository(@PathVariable int id) {
        return repositoryDAO.getRepository(id).orElse(null);
    }

    @RequestMapping(method = RequestMethod.POST)
    public int createRepository(String name, String description) {
        return repositoryDAO.createRepository(name, description, System.getProperty("user.name"));
    }
}

