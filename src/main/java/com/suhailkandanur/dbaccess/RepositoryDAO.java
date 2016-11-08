package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.FileSystem;
import com.suhailkandanur.entity.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.c;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class RepositoryDAO implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(RepositoryDAO.class);
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private final static String SELECT_REPO_BASE = "SELECT * FROM repository";
    private final static String SELECT_ACTIVE_REPO = SELECT_REPO_BASE + " where is_active = 1";
    private static final String SELECT_REPO_BY_REPOID = SELECT_REPO_BASE + " where id = ? ";


    private static final RowMapper<Repo> repositoryRowMapper = (rs, rowNum) -> {
        return new Repo(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("is_active"),
                rs.getString("created_by"),
                rs.getDate("creation_date"),
                rs.getString("updated_by"),
                rs.getDate("update_date"));
    };

    private static final RowMapper<FileSystem> fileSystemRowMapper = (rs, rowNum) -> {
        return new FileSystem(rs.getInt("id"),
                rs.getString("path"),
                rs.getBoolean("is_active"),
                rs.getString("created_by"),
                rs.getDate("creation_date"),
                rs.getString("updated_by"),
                rs.getDate("update_date"));
    };

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenIdDAO genIdDAO;

    @Override
    public void afterPropertiesSet() {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("repository");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<FileSystem> getFileSystemsForRepo(int repoId) {
        return this.jdbcTemplate.query("select fs.* from filesystem fs, repo_fs_mapping rfm " +
                " where fs.id = rfm.fs_id and rfm.repo_id = ? ", new Object[]{repoId}, fileSystemRowMapper);
    }

    private List<Repo> getRepositoryGeneric(String sql, Object... args) {
        logger.info("calling sql {} with args: {}", sql, args);
         return Optional.ofNullable(this.jdbcTemplate.query(sql, args == null ? null : new Object[]{args}, repositoryRowMapper))
                 .orElse(Collections.emptyList())
                 .parallelStream()
                 .map(repo -> { repo.addFileSystems(getFileSystemsForRepo(repo.getRepositoryId()), true); return repo;})
                 .collect(Collectors.toList());
    }
    public List<Repo> getAllRepositories(boolean activeOnly) {
        if (dataSource == null) {
            logger.error("data source is null");
        }
        if(activeOnly)
            return getRepositoryGeneric(SELECT_ACTIVE_REPO, null);
        return getRepositoryGeneric(SELECT_REPO_BASE, null);
    }

    public List<Repo> getAllRepositories() {
        return getAllRepositories(true);
    }

    public Optional<Repo> getRepository(int repoId) {
        List<Repo> repoList = getRepositoryGeneric(SELECT_REPO_BY_REPOID, repoId);
        if(repoList == null || repoList.isEmpty())
            return Optional.empty();
        if(repoList.size() > 1) {
            logger.error("multiple repositories found for the same id '{}'", repoId);
            throw new IllegalStateException("multiple repositories found for the same id '" + repoId + "'");
        }
        return Optional.ofNullable(repoList.get(0));
    }

    public int createRepository(Repo repo) {
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
                logger.info("repo with id '{}' created, no. of rows affected - {}", repo.getRepositoryId(), rowsInserted);
                return repo.getRepositoryId();
            }
        } catch(DataAccessException dae) {
            logger.error("unable to create repo, error: {}", dae.getMessage());
        }
        return -1;
    }

    public int createRepository(String name, String description, String requestor) {
        Date now = new Date();
        Repo repo = new Repo(genIdDAO.generateId("repository_id"), name, description, true, requestor, now, requestor, now);
        return createRepository(repo);
    }

    public void updateRepository(Repo repo) {

    }

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
