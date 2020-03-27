package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper.AccountRowMapper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SpringBatchTest
@ContextConfiguration(classes={IntTestConfig.class, AppConfig.class})
@ExtendWith(MockitoExtension.class)
@EnableGemFireMockObjects
@ClientCacheApplication
@EnableEntityDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
class Db2GeodeBatchApplicationTests {

	@Test
	void contextLoads() {
	}


	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("test")
	Region<String, Account> test;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void testJob() throws Exception {
		//jdbcTemplate.update(IO.readFile("src/test/resources/schema.sql"));
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


		Set<String> keys = test.keySetOnServer();
		System.out.println("keys:"+keys);

		//TODO: assertTrue(!keys.isEmpty());
		assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
	}

}
