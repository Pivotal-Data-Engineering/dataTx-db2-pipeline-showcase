package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.*;

/**
 * <pre>
 * The key to running GemFire cluster in embedded mode
 * is to not mix client and server Spring Data Geode configuration
 * in the same profile.
 *
 * In the following configuration, this client Geode configuration
 * is only loaded when the default Spring profile is used.
 *
 * Integration and unit test can exclude the default profile with
 * a expected external Geode cluster in order to use and embedded
 * version.
 *
 *
 * </pre>
 *
 * @author Gregory Green
 */
@Configuration
@ClientCacheApplication
@EnableSecurity
@EnableStatistics
@EnableLogging
@EnablePdx(serializerBeanName = "compositePdxSerializer")
@Profile("default")
public class GeodeClientConfig
{


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



}
