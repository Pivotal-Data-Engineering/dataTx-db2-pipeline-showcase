package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch;

import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.mapper.ResultSetMapRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.ColumnMapItemPreparedStatementSetter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author Gregory Green
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig
{
    @Value("${fetchSize:0}")
    private int fetchSize;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Value("${spring.batch.chunkSize}")
    private int chunkSize;

    @Value("${spring.batch.throttleLimit}")
    private int throttleLimit;


    @Value("${spring.datasourcetarget.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasourcetarget.url}")
    private String jdbcUrl;;

    @Value("${spring.datasourcetarget.username}")
    private String userName;

    @Value("${spring.datasourcetarget.password}")
    String password;


    DataSource systemOfRecordDataSource()

    {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(jdbcUrl)
                .username(userName)
                .password(password).build();
    }
    @Bean
    public ItemWriter<Map<String, ?>> writer(@Value("${spring.batch.insert.sql}")
                                              String sql,
                                             DataSource dataSource)
    {
        return new JdbcBatchItemWriterBuilder()
                .dataSource(dataSource)
                .sql(sql)
                .itemPreparedStatementSetter(new ColumnMapItemPreparedStatementSetter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader reader(ResultSetMapRowMapper rowMapper,
                                       @Value("${spring.batch.select.sql}")
                                                   String sql)
    {
        JdbcCursorItemReader reader = new JdbcCursorItemReader<>();
        reader.setRowMapper(rowMapper);
        reader.setDataSource(systemOfRecordDataSource());
        reader.setSql(sql);
        reader.setFetchSize(fetchSize);
        return reader;
    }

    @Bean
    public Job importJob(Step step1)
    {
        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                //.listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor()
    {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public Step step1(ItemWriter<Map<String, ?>> writer, ItemReader<Map<String, ?>> itemReader, TaskExecutor taskExecutor)
    {
        final TaskletStep step1;
        step1 = stepBuilderFactory.get("step1")
                .<Map<String, ?>, Map<String, ?>>chunk(chunkSize)
                .reader(itemReader)
                .writer(writer)
                .taskExecutor(taskExecutor)
                .throttleLimit(throttleLimit)
                .build();
        return step1;
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
