package com.suhailkandanur.dbaccess;

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
import java.util.*;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.c;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class RepositoryDAO implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(RepositoryDAO.class);
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

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

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenIdDAO genIdDAO;

    @Override
    public void afterPropertiesSet() {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("repository");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Repo> getAllRepositories() {
        if (dataSource == null) {
            logger.error("data source is null");
        }
        return this.jdbcTemplate.query("select * from repository", repositoryRowMapper);
    }

    public Optional<Repo> getRepository(int id) {
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject("select * from repository where id = ?", new Object[]{id}, repositoryRowMapper));
        } catch(EmptyResultDataAccessException dae) {
            logger.warn("repository for id {} not found", id);
            return Optional.empty();
        }
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
