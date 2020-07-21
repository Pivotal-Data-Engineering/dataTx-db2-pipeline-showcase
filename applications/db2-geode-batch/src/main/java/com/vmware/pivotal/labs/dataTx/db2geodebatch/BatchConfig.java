package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper.AccountRowMapper;
import io.pivotal.services.dataTx.spring.batch.geode.GeodeListPutAllWriter;
import org.apache.geode.cache.Region;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.function.Function;

/**
 * @author Gregory Green
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer
{
    @Value("${fetchSize:0}")
    private int fetchSize;


    @Value("${sql}")
    private String sql;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Bean
    public AccountRowMapper rowMapper()
    {
        return new AccountRowMapper();
    }

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemWriter<Account> writer(@Qualifier("test") Region<String, Account> test)
    {

        Function<Account, String> function = (Account account) -> account.getAccountNumber();

        return new GeodeListPutAllWriter<Account>(test, function);
    }

    @Bean
    public JdbcCursorItemReader reader(RowMapper<Account> rowMapper,
                                       DataSource dataSource)
    {
        JdbcCursorItemReader reader = new JdbcCursorItemReader<>();
        reader.setRowMapper(rowMapper);
        reader.setDataSource(dataSource);
        reader.setSql(sql);
        reader.setFetchSize(fetchSize);
        return reader;
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
    public Step step1(ItemWriter<Account> writer, DataSource dataSource)
    {
        return stepBuilderFactory.get("step1")
                .<Account, Account>chunk(10)
                .reader(reader(rowMapper(), dataSource))
                //.processor(processor())
                .writer(writer)
                .build();
    }


    @Bean
    public BatchConfigurer batchConfigurer()
    {
        return new DefaultBatchConfigurer()
        {
            @Override
            protected JobRepository createJobRepository()
            throws Exception
            {
                MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
                factory.setTransactionManager(new ResourcelessTransactionManager());
                return factory.getObject();
            }
        };
    }
}
