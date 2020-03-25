package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper.AccountRowMapper;
import io.pivotal.services.dataTx.spring.batch.geode.GeodeListPutAllWriter;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableLogging;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.config.annotation.EnableStatistics;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.function.Function;

/**
 * @author Gregory Green
 */
@Configuration
@EnableBatchProcessing()
@ClientCacheApplication
@EnableSecurity
@EnableStatistics
@EnableLogging
public class AppConfig
{
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean("test")
    public ClientRegionFactoryBean<?, ?> testRegion(GemFireCache gemfireCache)
    {

        ClientRegionFactoryBean testRegion = new ClientRegionFactoryBean<String, String>();
        testRegion.setCache(gemfireCache);
        testRegion.setDataPolicy(DataPolicy.EMPTY);
        return testRegion;
    }//------------------------------------------------

    @Bean
    public AccountRowMapper rowMapper()
    {
        return new AccountRowMapper();
    }//-------------------------------------------

    @Bean
    public JdbcCursorItemReader reader(RowMapper<Account> rowMapper,
                                       DataSource dataSource,
                                       Environment env)
    {
        JdbcCursorItemReader reader = new JdbcCursorItemReader<>();
        reader.setRowMapper(rowMapper);
        reader.setDataSource(dataSource);
        reader.setSql(env.getRequiredProperty("sql"));
        return reader;
    }

    @Bean
    public ItemWriter<Account> writer(Region<String, Account> region)
    {

        Function<Account, String> function = (Account account) -> account.getAccountNumber();

        return new GeodeListPutAllWriter<Account>(region, function);
    }

    @Bean
    public Job importUserJob(Step step1)
    {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                //.listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemWriter<Account> writer, DataSource dataSource, Environment env)
    {
        return stepBuilderFactory.get("step1")
                .<Account, Account>chunk(10)
                .reader(reader(rowMapper(), dataSource, env))
                //.processor(processor())
                .writer(writer)
                .build();
    }
}
