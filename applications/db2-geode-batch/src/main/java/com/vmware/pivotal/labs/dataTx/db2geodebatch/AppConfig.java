package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper.AccountRowMapper;
import io.pivotal.services.dataTx.spring.batch.geode.GeodeListPutAllWriter;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.*;
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
@EnablePdx(serializerBeanName = "compositePdxSerializer")
public class AppConfig
{
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public ReflectionBasedAutoSerializer pdxSerializer()
    {
        return new ReflectionBasedAutoSerializer(".*");
    }
    @Bean("test")
    public ClientRegionFactoryBean<String, Account> testRegion(GemFireCache gemfireCache)
    {

        ClientRegionFactoryBean testRegion = new ClientRegionFactoryBean<String, String>();
        testRegion.setCache(gemfireCache);
        testRegion.setRegionName("test");
        testRegion.setDataPolicy(DataPolicy.EMPTY);
        return testRegion;
    }//------------------------------------------------

    @Bean
    public AccountRowMapper rowMapper()
    {
        return new AccountRowMapper();
    }//-------------------------------------------

    @Bean
    public BatchConfigurer batchConfigurer() {
        return new DefaultBatchConfigurer() {
            @Override
            protected JobRepository createJobRepository() throws Exception {
                MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
                factory.setTransactionManager(new ResourcelessTransactionManager());
                return factory.getObject();
            }
        };
    }
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
    public ItemWriter<Account> writer(@Qualifier("test") Region<String, Account> test)
    {

        Function<Account, String> function = (Account account) -> account.getAccountNumber();

        return new GeodeListPutAllWriter<Account>(test, function);
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
