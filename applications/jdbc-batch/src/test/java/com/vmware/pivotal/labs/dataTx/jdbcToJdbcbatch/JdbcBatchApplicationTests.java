package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch;

import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.mapper.ResultSetMapRowMapper;
import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.processor.ExistingAccountFilter;
import nyla.solutions.core.exception.CommunicationException;
import nyla.solutions.core.io.IO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles({"test", "batch"})
class JdbcBatchApplicationTests {
    JdbcTemplate sourceTemplate;
    JdbcTemplate primaryTemplate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;


    @BeforeEach
    public void setUp() throws IOException {
        String sql = IO.readFile("src/test/resources/set-up.sql");
        this.sourceTemplate.execute(sql);
        this.primaryTemplate.execute(sql);

        for (int i = 1; i <= 3; i++) {
            sourceTemplate.update("insert into SOURCE_ACCOUNT( test1," +
                            " test2, test3" +
                            ") values (?,?,?)",
                    "account" + i, "location1-" + i, "location2-" + i);
        }

    }

    @AfterEach
    void tearDown() {
        try {
            sourceTemplate.update("delete from SOURCE_ACCOUNT");
        } catch (Exception e) {
        }
        try {

            primaryTemplate.update("delete from DEST_ACCOUNT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Autowired
    public void setReaderDataSource(@Qualifier("source") DataSource dataSource) {
        this.sourceTemplate = new JdbcTemplate(dataSource);
    }


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.primaryTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void retryRetries() throws Exception {

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        while (jobExecution.isRunning()) {
            Thread.sleep(50);
        }

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

    }
}
