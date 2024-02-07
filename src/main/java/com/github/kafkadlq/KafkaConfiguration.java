package com.github.kafkadlq;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;


@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        var props = kafkaProperties.buildConsumerProperties(new DefaultSslBundleRegistry());
        props.remove(ConsumerConfig.CLIENT_ID_CONFIG);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(final ConsumerFactory<String, String> consumerFactory,
                                                                                                 final CommonErrorHandler commonErrorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(commonErrorHandler);
        return factory;
    }

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
