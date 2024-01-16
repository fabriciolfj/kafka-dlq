package com.github.kafkadlq;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;


@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        final var config = kafkaProperties.buildConsumerProperties(new DefaultSslBundleRegistry());
        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new StringSerializer());
    }

    @Bean
    public KafkaTemplate<String, String> retryableTopicKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
