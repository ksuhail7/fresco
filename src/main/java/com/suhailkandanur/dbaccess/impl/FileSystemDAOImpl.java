package com.suhailkandanur.dbaccess.impl;

import com.suhailkandanur.dbaccess.FileSystemDAO;
import com.suhailkandanur.dbaccess.GenIdDAO;
import com.suhailkandanur.entity.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by suhail on 2016-11-09.
 */
@Service
public class FileSystemDAOImpl implements FileSystemDAO {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemDAOImpl.class);

    private SimpleJdbcInsert fileSystemInsertCall;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GenIdDAO genIdDAO;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fileSystemInsertCall = new SimpleJdbcInsert(dataSource).withTableName("filesystem");
    }


    @Override
    public List<FileSystem> getAllFileSystems(boolean includeInactive) {
        final List<FileSystem> query = this.jdbcTemplate.query(SELECT_FS_QUERY_BASE, fileSystemRowMapper);
        if (includeInactive)
            return query;
        return query.stream().filter(fs -> fs.isActive()).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public FileSystem getFileSystem(int fileSysId) {
        return getFileSystemGeneric(SELECT_FS_QUERY_BY_ID, new Object[]{fileSysId});
    }

    @Override
    public FileSystem getFileSystem(String path) {
        logger.info("finding filesystem for path {}", path);
        final FileSystem fileSystem = getFileSystemGeneric(SELECT_FS_QUERY_BY_PATH, new Object[]{path});
        if (logger.isDebugEnabled()) {
            if (fileSystem != null) {
                logger.debug("filesystem for path {} found, fs id: {}", path, fileSystem.getFilesystemId());
            } else {
                logger.debug("filesystem path {} is not defined.");
            }
        }
        return fileSystem;
    }

    @Override
    public FileSystem createFileSystem(String path, String requester) {
        if (getFileSystem(path) != null)
            throw new UnsupportedOperationException("filesystem path already exists");
        int fsId = genIdDAO.generateId("filesystem_id");
        Date now = new Date();
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("id", fsId)
                .addValue("path", path)
                .addValue("is_active", true)
                .addValue("created_by", requester)
                .addValue("creation_date", now)
                .addValue("updated_by", requester)
                .addValue("update_date", now);
        final int noOfRows = this.fileSystemInsertCall.execute(source);
        if (noOfRows < 1) {
            logger.error("unable to insert record into filesystem table, check previous errors");
            return null;
        }
        return new FileSystem(fsId, path, true, requester, now, requester, now);
    }

    private FileSystem getFileSystemGeneric(String sql, Object[] args) {
        final List<FileSystem> fileSystemList = this.jdbcTemplate.query(sql, args, fileSystemRowMapper);
        if (fileSystemList != null && fileSystemList.size() > 1) {
            throw new IllegalStateException("multiple filesystems found for parameter" + args);
        }

        return (fileSystemList != null && fileSystemList.size() == 1) ? fileSystemList.get(0) : null;
    }
}
