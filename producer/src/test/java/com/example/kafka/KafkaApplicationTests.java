package com.example.kafka;

import com.example.kafka.domain.Book;
import com.example.kafka.event.LibraryEvent;
import com.example.kafka.event.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 4)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class KafkaApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private EmbeddedKafkaBroker broker;

	private Consumer<Integer, String> consumer;


	@BeforeEach
	public void before(){
		Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("groupTest", "true", broker));
		consumer = new DefaultKafkaConsumerFactory(configs, new IntegerDeserializer(), new StringDeserializer())
				.createConsumer();
		broker.consumeFromAllEmbeddedTopics(consumer);
	}

	@AfterEach
	public void after(){
		consumer.close();
	}

	@Test
	public void postLibraryEvent() {
		//given
		Book book = Book.builder().id(1).name("meu-livro").build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", MediaType.APPLICATION_JSON.toString());
		HttpEntity<Book> request = new HttpEntity<>(book,headers);

		//when
		ResponseEntity<Book> responseEntity = restTemplate.exchange("/v1/book",
				HttpMethod.POST,
				request,
				Book.class);


		ConsumerRecord<Integer, String> record = KafkaTestUtils.getSingleRecord(consumer, "library-events");
		String expectedValue = "{\"libraryEventId\":null,\"type\":\"NEW\",\"book\":{\"id\":1,\"name\":\"meu-livro\"}}";

		//then
		assertEquals(HttpStatus.ACCEPTED,responseEntity.getStatusCode());
		assertEquals(record.value(), expectedValue);
	}

}
