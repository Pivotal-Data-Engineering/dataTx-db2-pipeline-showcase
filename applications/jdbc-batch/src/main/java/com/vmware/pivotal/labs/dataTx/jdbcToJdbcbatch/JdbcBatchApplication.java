package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@SpringBootApplication
@EnableTask
public class JdbcBatchApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(JdbcBatchApplication.class, args);
    }

}
