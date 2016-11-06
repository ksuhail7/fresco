package com.suhailkandanur.dbaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

/**
 * Created by suhail on 2016-11-05.
 */
@Repository
public class GenIdDAO implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(GenIdDAO.class);
    @Autowired
    public DataSource dataSource;
    private SimpleJdbcCall jdbcCall;

    public int generateId(String parameter) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("parameter", parameter);
        Map output = jdbcCall.execute(Integer.class, in);

        return (int) output.get("value");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.jdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("gen_id")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("parameter")
                .declareParameters(
                        new SqlParameter("parameter", Types.VARCHAR),
                        new SqlOutParameter("value", Types.INTEGER)
                );
    }
}
