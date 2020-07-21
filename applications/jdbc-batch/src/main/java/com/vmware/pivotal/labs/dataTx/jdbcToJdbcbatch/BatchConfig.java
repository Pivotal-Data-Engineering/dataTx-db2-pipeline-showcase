package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch;

import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.mapper.ResultSetMapRowMapper;
import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.processor.ExistingAccountFilter;
import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository.AccountRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.ColumnMapItemPreparedStatementSetter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    @Value("${spring.datasource-reader.driverClassName}")
    private String readerDriverClassName;

    @Value("${spring.datasource-reader.url}")
    private String readerJdbcUrl;;

    @Value("${spring.datasource-reader.username}")
    private String readerUserName;

    @Value("${spring.datasource-reader.password}")
    private String readerPassword;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    String password;

    DataSource primaryDataSource()
    {
        return DataSourceBuilder.create()
                .url(url)
                .driverClassName(driverClassName)
                .username(username)
                .password(password)
                .build();
    }

    DataSource readerDataSource()

    {
        return DataSourceBuilder.create()
                .driverClassName(readerDriverClassName)
                .url(readerJdbcUrl)
                .username(readerUserName)
                .password(readerPassword).build();
    }

    @Bean("itemProcessor")
    public ItemProcessor<Map<String,?>,Map<String,?>> itemProcessor(AccountRepository accountRepository)
    {
        return new ExistingAccountFilter(accountRepository);
    }//-------------------------------------------
    @Bean
    public ItemWriter<Map<String, Object>> writer(@Value("${spring.batch.insert.sql}")
                                              String sql)
    {
        ItemWriter<Map<String,Object>> writer= new JdbcBatchItemWriterBuilder()
                .dataSource(primaryDataSource())
                .sql(sql)
                .itemPreparedStatementSetter(new ColumnMapItemPreparedStatementSetter())
                .build();

        return writer;
    }

    @Bean
    public JdbcCursorItemReader<Map<String,?>> reader(ResultSetMapRowMapper rowMapper,
                                       @Value("${spring.batch.select.sql}")
                                                   String sql)
    {
        JdbcCursorItemReader reader = new JdbcCursorItemReader<>();
        reader.setRowMapper(rowMapper);
        reader.setDataSource(readerDataSource());
        reader.setSql(sql);
        reader.setFetchSize(fetchSize);
        return reader;
    }

    @Bean
    public Job importJob(Step step1)
    {
        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
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
    public Step step1(ItemWriter<Map<String, ?>> writer,
                      ItemReader<Map<String, ?>> itemReader,
                      @Qualifier("itemProcessor")
                      ItemProcessor<Map<String, ?>,Map<String, ?>> itemProfessor,
                      TaskExecutor taskExecutor)
    {
        final TaskletStep step1;
        step1 = stepBuilderFactory.get("step1")
                .<Map<String, ?>, Map<String, ?>>chunk(chunkSize)
                .reader(itemReader)
                .processor((ItemProcessor)itemProfessor)
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
