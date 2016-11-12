package com.suhailkandanur.dbaccess.impl;

import com.suhailkandanur.dbaccess.RepoFileSystemMappingDAO;
import com.suhailkandanur.entity.RepoFsMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * Created by suhail on 2016-11-11.
 */
@Repository
public class RepoFileSystemMappingDAOImpl implements InitializingBean, RepoFileSystemMappingDAO {

    private static final Logger logger = LoggerFactory.getLogger(RepoFileSystemMappingDAOImpl.class);

    @Autowired
    private DataSource dataSource;

    private SimpleJdbcInsert jdbcInsert;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("repo_fs_mapping");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    @Transactional
    public RepoFsMapping createMapping(int repoId, int fsId, String requester) {
        if (isRepoMappedToFileSystem(repoId, fsId))
            throw new RuntimeException("repository [" + repoId + "] filesystem [" + fsId + "] mapping already exists");
        Date now = new Date();
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("repo_id", repoId)
                .addValue("fs_id", fsId)
                .addValue("is_active", true)
                .addValue("created_by", requester)
                .addValue("creation_date", now)
                .addValue("updated_by", requester)
                .addValue("update_date", now);
        final int noOfRows = this.jdbcInsert.execute(source);
        if (noOfRows != 1) {
            logger.error("unable to map repo id {} with filesystem id {}", repoId, fsId);
            throw new RuntimeException("mapping already exists");
        }
        return new RepoFsMapping(repoId, fsId, true, requester, now, requester, now);
    }

    @Override
    public boolean deleteMapping(int repoId, int fsId, String requester) {
        if (!isRepoMappedToFileSystem(repoId, fsId)) {
            logger.info("no mapping exists for repository id {} and filesystem id {}", repoId, fsId);
            return true;
        }

        final int deleteCount = this.jdbcTemplate.update(DELETE_REPO_FS_MAPPING, repoId, fsId);
        return (deleteCount == 1);
    }

    @Override
    public RepoFsMapping getRepoFsMapping(int repoId, int fsId) {
        final List<RepoFsMapping> repoFsMapList = getRepoFsMappingGeneric(QUERY_REPO_FS_MAPPING, new Object[]{repoId, fsId});
        if (repoFsMapList != null && repoFsMapList.size() > 1) {
            logger.error("multiple entries found for repository {} mapped to same filesystem {}", repoId, fsId);
            throw new IllegalStateException("multiple entries for repository-filesystem mapping");
        }
        return (repoFsMapList != null && repoFsMapList.size() == 1) ? repoFsMapList.get(0) : null;

    }

    @Override
    public List<RepoFsMapping> getMappedFileSystems(int repoId) {
        return getRepoFsMappingGeneric(QUERY_FS_BY_REPOID, new Object[]{repoId});
    }

    @Override
    public List<RepoFsMapping> getMappedRepositories(int fsId) {
        return getRepoFsMappingGeneric(QUERY_REPO_BY_FSID, new Object[]{fsId});
    }

    private List<RepoFsMapping> getRepoFsMappingGeneric(String sql, Object[] args) {
        return this.jdbcTemplate.query(sql, args, repoFsRowMapper);
    }
}
