package com.vmware.pivotal.labs.dataTx.db2geodebatch;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableLocator;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;

/**
 * <pre>
 *     This class autowires an an embedded Apach Geode cluster when the
 *     "test" profile is active.
 *     An CacheServer and Locator will be started in embedded mode.
 *
 * </pre>
 *
 * @author Gregory Green
 */
@Configuration
@Profile("test")
@PeerCacheApplication
@EnableLocator
@EnablePdx
public class EmbeddedGeodeConfig
{
    @Bean
    public ReflectionBasedAutoSerializer pdxSerializer()
    {
        return new ReflectionBasedAutoSerializer(".*");
    }

    @Bean("embedded")
    CacheFactoryBean cache()
    {
        CacheFactoryBean bean = new CacheFactoryBean();
        return bean;
    }

    @Bean("test")
    PartitionedRegionFactoryBean region(GemFireCache gemfireCache)
    {
        PartitionedRegionFactoryBean<Long, Account> region =
                new PartitionedRegionFactoryBean<>();

        region.setCache(gemfireCache);
        region.setClose(false);
        region.setPersistent(false);

        return region;
    }
}
