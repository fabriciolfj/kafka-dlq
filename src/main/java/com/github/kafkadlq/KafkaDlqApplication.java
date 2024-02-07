package com.github.kafkadlq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.CommonErrorHandler;

@SpringBootApplication
public class KafkaDlqApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDlqApplication.class, args);
	}

}
