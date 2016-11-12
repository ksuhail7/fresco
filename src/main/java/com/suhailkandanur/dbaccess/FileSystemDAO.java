package com.suhailkandanur.dbaccess;

import com.suhailkandanur.entity.FileSystem;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by suhail on 2016-11-10.
 */
public interface FileSystemDAO {
    String SELECT_FS_QUERY_BASE = "select * from filesystem";
    String SELECT_FS_QUERY_BY_ID = SELECT_FS_QUERY_BASE + " where id = ? ";
    String SELECT_FS_QUERY_BY_PATH = SELECT_FS_QUERY_BASE + " where path = ?";

    RowMapper<FileSystem> fileSystemRowMapper = (rs, rowNum) -> new FileSystem(rs.getInt("id"),
            rs.getString("path"),
            rs.getBoolean("is_active"),
            rs.getString("created_by"),
            rs.getDate("creation_date"),
            rs.getString("updated_by"),
            rs.getDate("update_date"));

    List<FileSystem> getAllFileSystems(boolean includeInactive);

    default List<FileSystem> getAllFileSystems() {
        return getAllFileSystems(false);
    }

    FileSystem getFileSystem(int fileSysId);

    FileSystem getFileSystem(String path);

    FileSystem createFileSystem(String path, String requester);

    default FileSystem getOrCreateFileSystem(String path, String requester) {
        FileSystem fs = getFileSystem(path);
        return (fs == null) ? createFileSystem(path, requester) : fs;
    }


}