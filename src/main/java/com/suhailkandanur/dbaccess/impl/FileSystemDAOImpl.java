package com.suhailkandanur.dbaccess.impl;

import com.suhailkandanur.dbaccess.FileSystemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

/**
 * Created by suhail on 2016-11-09.
 */
public class FileSystemDAOImpl implements InitializingBean, FileSystemDAO {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemDAOImpl.class);

    private SimpleJdbcInsert fileSystemInsertCall;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Override
    public void afterPropertiesSet() {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fileSystemInsertCall = new SimpleJdbcInsert(dataSource).withTableName("filesystem");
    }


}
