package com.github.kafkadlq;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest(classes = KafkaDlqApplication.class)
class KafkaDlqApplicationTests {

	@Container
	private static KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

	@DynamicPropertySource
	static void setProps(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
	}

	@Autowired
	PersonService personService;

	static KafkaProducer<String, String> testKafkaProducer;

	@BeforeAll
	static void beforeAll() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
		testKafkaProducer = new KafkaProducer<>(props);
	}

	@Test
	void testSendMessageSuccess() {
		var json = "{ \"name\" : \"fabricio\", \"code\": \"111\"}";
		final ProducerRecord<String, String> record = new ProducerRecord<>("persons", json);

		testKafkaProducer.send(record);

		await().untilAsserted(() -> assertFalse(personService.getPersons().isEmpty()));
	}

	@Test
	void testSendMessageInvalid() {
		var messageOne = "{ name : fabricio code: 111}";
		var messageTwo = "{ \"name\" : \"fabricio\", \"code\": \"111\"}";

		final ProducerRecord<String, String> record = new ProducerRecord<>("persons", messageOne);
		testKafkaProducer.send(record);

		final ProducerRecord<String, String> recordTwo = new ProducerRecord<>("persons", messageTwo);
		testKafkaProducer.send(recordTwo);

		await().untilAsserted(() -> assertFalse(personService.getPersons().isEmpty()));
	}
}
