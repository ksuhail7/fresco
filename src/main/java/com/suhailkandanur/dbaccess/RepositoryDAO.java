package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.Repo;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by suhail on 2016-11-10.
 */
public interface RepositoryDAO {

    final static String SELECT_REPO_BASE = "SELECT * FROM repository";
    final static String SELECT_ACTIVE_REPO = SELECT_REPO_BASE + " where is_active = true";
    static final String SELECT_REPO_BY_REPOID = SELECT_REPO_BASE + " where id = ? ";
    static final String SELECT_REPO_BY_NAME = SELECT_REPO_BASE + " where name = ? ";

    static final RowMapper<Repo> repositoryRowMapper = (rs, rowNum) -> {
        return new Repo(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("is_active"),
                rs.getString("created_by"),
                rs.getDate("creation_date"),
                rs.getString("updated_by"),
                rs.getDate("update_date"));
    };

    List<Repo> getAllRepositories(boolean includeInactive);

    default List<Repo> getAllRepositories() {
        return getAllRepositories(false);
    }

    Repo getRepository(int repoId);

    Repo getRepository(String name);

    Repo createRepository(String name, String description, String requester);

    boolean deleteRepository(int id);
}
