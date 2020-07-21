package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
@SpringBootApplication
public class KakaConnectJdbcConsumerSinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(KakaConnectJdbcConsumerSinkApplication.class, args);
	}

}
