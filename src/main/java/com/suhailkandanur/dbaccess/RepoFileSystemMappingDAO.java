package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.RepoFsMapping;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by suhail on 2016-11-11.
 */
public interface RepoFileSystemMappingDAO {
    RowMapper<RepoFsMapping> repoFsRowMapper = (rs, rowNum) -> new RepoFsMapping(rs.getInt("repo_id"),
            rs.getInt("fs_id"), rs.getBoolean("is_active"), rs.getString("created_by"),
            rs.getDate("creation_date"),
            rs.getString("updated_by"),
            rs.getDate("update_date"));
    String QUERY_REPO_FS_MAPPING_BASE = "select * from repo_fs_mapping ";
    String QUERY_REPO_FS_MAPPING = QUERY_REPO_FS_MAPPING_BASE + " where repo_id = ? and fs_id = ?";
    String QUERY_FS_BY_REPOID = QUERY_REPO_FS_MAPPING_BASE + " where repo_id = ?";
    String QUERY_REPO_BY_FSID = QUERY_REPO_FS_MAPPING_BASE + " where fs_id = ?";
    String DELETE_REPO_FS_MAPPING = "delete from repo_fs_mapping where repo_id = ? and fs_id = ?";

    RepoFsMapping createMapping(int repoId, int fsId, String requester);

    boolean deleteMapping(int repoId, int fsId, String requester);

    default boolean isRepoMappedToFileSystem(int repoId, int fsId) {
        return getRepoFsMapping(repoId, fsId) != null;
    }

    RepoFsMapping getRepoFsMapping(int repoId, int fsId);

    List<RepoFsMapping> getMappedFileSystems(int repoId);

    List<RepoFsMapping> getMappedRepositories(int fsId);
}
