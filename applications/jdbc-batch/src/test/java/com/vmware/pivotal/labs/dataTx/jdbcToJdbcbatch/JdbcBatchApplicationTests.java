package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch;

import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.mapper.ResultSetMapRowMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BatchConfig.class,
        EmbeddedH2Config.class,
        ResultSetMapRowMapper.class, JdbcBatchApplication.class}
)
@SpringBatchTest
@ActiveProfiles({"test", "batch"})
@EnableJpaRepositories

class JdbcBatchApplicationTests
{
    JdbcTemplate sourceTemplate;
    JdbcTemplate primaryTemplate;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    //@Test
    void contextLoads()
    {
    }

    @BeforeEach
    public void setUp()
    {
        this.tearDown();
    }

    @AfterEach
    void tearDown()
    {
        sourceTemplate.update("delete from SOURCE_ACCOUNT");
        primaryTemplate.update("delete from DEST_ACCOUNT");
    }


    @Autowired
    public void setReaderDataSource(@Qualifier("source") DataSource dataSource)
    {
        this.sourceTemplate = new JdbcTemplate(dataSource);
    }


    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        this.primaryTemplate = new JdbcTemplate(dataSource);
    }


    @Test
    public void testJob()
    throws Exception
    {

        for (int i = 1; i <= 10; i++)
        {
            sourceTemplate.update("insert into SOURCE_ACCOUNT( test1," +
                            " test2, test3" +
                            ") values (?,?,?)",
                    "account" + i, "location1-" + i, "location2-" + i);
        }

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        while (jobExecution.isRunning())
        {
            Thread.sleep(50);
        }

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        int count = primaryTemplate.queryForObject("select count(*) from DEST_ACCOUNT", Integer.class);
        System.out.println("count:" + count);

        assertEquals(10, count);
        Thread.sleep(1000);
    }
}
