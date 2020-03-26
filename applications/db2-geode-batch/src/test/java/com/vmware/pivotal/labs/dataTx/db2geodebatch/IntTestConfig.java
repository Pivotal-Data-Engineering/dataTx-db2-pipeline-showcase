package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import org.apache.geode.cache.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Gregory Green
 */
@Configuration
@EnableBatchProcessing
public class IntTestConfig
{
    //@Bean
    //public Cache cache()
    //{
    //    return new CacheFactory().create();
    //}

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:TEST");
        return dataSourceBuilder.build();
    }

   /* @Bean
    public Region<String, Account> region(Cache cache)
    {
        RegionFactory<String,Account> rf = cache.createRegionFactory(RegionShortcut.LOCAL);
        return rf.create("test");
    }
    */

}
