package com.suhailkandanur.dbaccess.impl;

import com.suhailkandanur.dbaccess.GenIdDAO;
import com.suhailkandanur.dbaccess.RepositoryDAO;
import com.suhailkandanur.entity.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class RepositoryDAOImpl implements InitializingBean, RepositoryDAO {

    private static Logger logger = LoggerFactory.getLogger(RepositoryDAOImpl.class);
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenIdDAO genIdDAO;

    @Override
    public void afterPropertiesSet() {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("repository");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private List<Repo> getRepositoryGeneric(String sql, Object[] args) {
        logger.info("calling sql {} with args: {}", sql, args);
        return Optional.ofNullable(this.jdbcTemplate.query(sql, args, repositoryRowMapper))
                .orElse(Collections.emptyList());

    }

    @Override
    public List<Repo> getAllRepositories(boolean includeInActive) {
        if (includeInActive) {
            return getRepositoryGeneric(SELECT_REPO_BASE, null);
        }
        return getRepositoryGeneric(SELECT_ACTIVE_REPO, null);

    }

    @Override
    public Repo getRepository(int repoId) {
        List<Repo> repoList = getRepositoryGeneric(SELECT_REPO_BY_REPOID, new Object[]{repoId});
        if (repoList != null && repoList.size() > 1) {
            logger.error("multiple repositories found for the same id '{}'", repoId);
            throw new IllegalStateException("multiple repositories found for the same id '" + repoId + "'");
        }
        return (repoList != null && repoList.size() == 1) ? repoList.get(0) : null;
    }

    @Override
    public Repo getRepository(String name) {
        List<Repo> repoList = getRepositoryGeneric(SELECT_REPO_BY_NAME, new Object[]{name});
        if (repoList != null && repoList.size() > 1) {
            logger.error("multiple repositories found for the same id '{}'", name);
            throw new IllegalStateException("multiple repositories found for the same id '" + name + "'");
        }
        return (repoList != null && repoList.size() == 1) ? repoList.get(0) : null;
    }

    private Repo createRepository(Repo repo) {
        try {
            Map<String, Object> parameters = new HashMap<>(8);
            parameters.put("id", repo.getRepositoryId());
            parameters.put("name", repo.getName());
            parameters.put("description", repo.getDescription());
            parameters.put("creation_date", repo.getCreationTime());
            parameters.put("is_active", repo.isActive());
            parameters.put("created_by", repo.getCreatedBy());
            parameters.put("update_date", repo.getUpdateTime());
            parameters.put("updated_by", repo.getUpdatedBy());
            int rowsInserted = this.jdbcInsert.execute(parameters);
            if (rowsInserted == 1) {
                logger.info("repo with id '{}' created, no. of rows affected {}", repo.getRepositoryId(), rowsInserted);
                return repo;
            }
        } catch (DataAccessException dae) {
            logger.error("unable to create repo, error: {}", dae.getMessage());
        }
        return null;
    }

    @Override
    public Repo createRepository(String name, String description, String requestor) {
        Objects.requireNonNull(requestor);
        Objects.requireNonNull(name);
        Date now = new Date();
        Repo repo = new Repo(genIdDAO.generateId("repository_id"), name, description, true, requestor, now, requestor, now);
        return createRepository(repo);
    }

    @Override
    public boolean deleteRepository(int id) {
        try {
            int rowsDeleted = this.jdbcTemplate.update("delete from repository where id = ?", id);
            if (rowsDeleted == 1) {
                logger.info("repository with id '{}' deleted successfully, no. of records deleted: {}", id, rowsDeleted);
            } else {
                logger.warn("{} records deleted, repository with id '{}' not found", rowsDeleted);
            }
            return true;
        } catch (DataAccessException dae) {
            logger.error("error deleting repository with id '{}', exception: {}", id, dae.getMessage());
        }
        return false;
    }
}

