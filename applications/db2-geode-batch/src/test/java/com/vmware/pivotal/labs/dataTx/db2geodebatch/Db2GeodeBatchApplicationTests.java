package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper.AccountRowMapper;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//, properties = {"spring.main.allow-bean-definition-overriding=true"
@SpringBootTest(classes = {EmbeddedGeodeConfig.class,BatchConfig.class,
        EmbeddedH2Config.class}
)
@SpringBatchTest
@ActiveProfiles({"test","batch"})
class Db2GeodeBatchApplicationTests
{

    @Test
    void contextLoads()
    {
    }


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("test")
    Region<String, Account> test;

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testJob()
    throws Exception
    {
        jdbcTemplate.update("CREATE TABLE ACCOUNT (\n" +
                "  " + AccountRowMapper.ACCOUNT_NBR_NM + " VARCHAR ,\n" +
                "    " + AccountRowMapper.LOC_ADDR1 + " VARCHAR,\n" +
                "    " + AccountRowMapper.LOC_ADDR2 + " VARCHAR\n" +
                ");");

        jdbcTemplate.update("delete from ACCOUNT");

        for (int i = 1; i <= 10; i++)
        {
            jdbcTemplate.update("insert into ACCOUNT(" + AccountRowMapper.ACCOUNT_NBR_NM +
                            ", " + AccountRowMapper.LOC_ADDR1 +
                            ", " + AccountRowMapper.LOC_ADDR2 +
                            ") values (?,?,?)",
                    "account" + i, "location1-" + i, "location2-" + i);
        }

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        while (jobExecution.isRunning())
        {
            Thread.sleep(50);
        }

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        Set<String> keys = test.keySet();
        System.out.println("keys:" + keys);

        assertTrue(!keys.isEmpty());
        Thread.sleep(1000);
    }
}
