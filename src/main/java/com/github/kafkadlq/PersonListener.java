package com.github.kafkadlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonListener {

    private static final Random RANDOM = new Random();
    private final ObjectMapper mapper;

    @KafkaListener(topics = { "persons"}, groupId = "persons")
    @RetryableTopic(attempts = "1", kafkaTemplate = "retryableTopicKafkaTemplate", dltStrategy = DltStrategy.FAIL_ON_ERROR) //representa o número de tentativas antes de enviar a dlq
    public void handlePerson(final String person, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws JsonProcessingException {
        if (RANDOM.nextInt() % 2 == 0) {
            throw new RuntimeException();
        }

        var value = mapper.readValue(person, Person.class);
        log.info("receive {}, source topic {}", value, topic);
    }

    @DltHandler
    public void handleDltPerson(final String person, @Header(KafkaHeaders.RECEIVED_TOPIC) final String topic) {
        log.info("receive dlt {}, source topic {}", person, topic);
    }
}
