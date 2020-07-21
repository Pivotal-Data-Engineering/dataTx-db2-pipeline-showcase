package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Gregory Green
 */
@Configuration
public class AppConfig
{
    @Value("classpath:hello.txt")
    Resource value;


    @Bean
    Account account()
    throws IOException
    {
        Account account = Account.builder().accountName
                (new String(
                        Files.readAllBytes(value.getFile().toPath())))
                .build();


        return account;
    }

}
