package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch;

import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.mapper.ResultSetMapRowMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BatchConfig.class,
        EmbeddedH2Config.class,
        ResultSetMapRowMapper.class}
)
@SpringBatchTest
@ActiveProfiles({"test", "batch"})
class JdbcBatchApplicationTests
{

    @Test
    void contextLoads()
    {
    }

    @BeforeEach
    public void setUp()
    {
        /*
        jdbcTemplate.update("CREATE TABLE SOURCE_ACCOUNT" +
                " (\n" +
                "  test1 VARCHAR ,\n" +
                "  test2 VARCHAR ,\n" +
                "    test3 VARCHAR\n" +
                ");");


        jdbcTemplate.update("CREATE TABLE DEST_ACCOUNT" +
                " (\n" +
                "  test1 VARCHAR ,\n" +
                "  test2 VARCHAR ,\n" +
                "    test3 VARCHAR\n" +
                ");");

         */

        this.tearDown();
    }

    @AfterEach
    void tearDown()
    {
        jdbcTemplate.update("delete from SOURCE_ACCOUNT");
        jdbcTemplate.update("delete from DEST_ACCOUNT");
    }

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    JdbcTemplate jdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testJob()
    throws Exception
    {

        for (int i = 1; i <= 10; i++)
        {
            jdbcTemplate.update("insert into SOURCE_ACCOUNT( test1," +
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

        int count = jdbcTemplate.queryForObject("select count(*) from DEST_ACCOUNT", Integer.class);
        System.out.println("count:" + count);

        assertEquals(10, count);
        Thread.sleep(1000);
    }
}
