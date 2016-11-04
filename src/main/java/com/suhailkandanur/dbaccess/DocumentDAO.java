package com.suhailkandanur.dbaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

/**
 * Created by suhail on 2016-11-03.
 */
@Repository
public class DocumentDAO {
    private SimpleJdbcInsert insertJdbcCall;
    private SimpleJdbcCall queryJdbcCall;


}
