package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import static org.junit.jupiter.api.Assertions.*;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper.AccountRowMapper;
import nyla.solutions.core.io.IO;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Set;

@SpringBootTest
@SpringBatchTest
//@RunWith(SpringRunner.class)
@ContextConfiguration(classes={IntTestConfig.class, AppConfig.class})
@ExtendWith(MockitoExtension.class)
class Db2GeodeBatchApplicationTests {

	@Test
	void contextLoads() {
	}

	@Nested
	public class Given_service
	{

	}

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void testJob() throws Exception {
		jdbcTemplate.update(IO.readFile("src/test/resources/schema.sql"));
		jdbcTemplate.update("CREATE TABLE ACCOUNT (\n" +
				"  "+ AccountRowMapper.ACCOUNT_NBR_NM +" VARCHAR ,\n" +
				"    "+AccountRowMapper.LOC_ADDR1+" VARCHAR,\n" +
				"    "+AccountRowMapper.LOC_ADDR2+" VARCHAR\n" +
				");");

		jdbcTemplate.update("delete from ACCOUNT");

		for (int i = 1; i <= 10; i++) {
			jdbcTemplate.update("insert into ACCOUNT("+AccountRowMapper.ACCOUNT_NBR_NM+
							", "+AccountRowMapper.LOC_ADDR1 +
							", "+AccountRowMapper.LOC_ADDR2 +
					") values (?,?,?)",
					"account"+i,"location1-"+i,"location2-"+i);
		}

		JobExecution jobExecution = jobLauncherTestUtils.launchJob();

		Region<String, Account> region = CacheFactory.getAnyInstance().getRegion("ACCOUNT");
		Set<String> keys = region.keySet();
		System.out.println("keys:"+keys);

		assertTrue(!keys.isEmpty());
		assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
	}

}
