package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TaskConfig extends DefaultTaskConfigurer {

    public TaskConfig(@Qualifier("default") DataSource dataSource) {
        super(dataSource);
    }

}
