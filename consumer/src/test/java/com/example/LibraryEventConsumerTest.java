package com.example;

import com.example.kafka.KafkaApplication;
import com.example.kafka.consumer.LibraryEventConsumer;
import com.example.kafka.entity.BookEntity;
import com.example.kafka.entity.LibraryEntity;
import com.example.kafka.entity.LibraryEventType;
import com.example.kafka.repository.LibraryEventRepository;
import com.example.kafka.service.LibraryEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = KafkaApplication.class)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@ActiveProfiles("test")
public class LibraryEventConsumerTest {

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @SpyBean
    private LibraryEventConsumer libraryEventConsumer;

    @SpyBean
    private LibraryEventService libraryEventService;

    @Autowired
    private LibraryEventRepository repository;

    @BeforeEach
    public void beforeEach(){
        for (MessageListenerContainer listener : registry.getListenerContainers()){
            ContainerTestUtils.waitForAssignment(listener, broker.getPartitionsPerTopic());
        }
    }

    @AfterEach
    public void afterEach(){
        repository.deleteAll();
    }

    @Test
    public void shouldSaveOnDatabase() throws ExecutionException, InterruptedException, JsonProcessingException {

        String json = "{\"libraryEventId\":null,\"type\":\"NEW\",\"book\":{\"id\":null,\"name\":\"meu-livro\"}}";
        kafkaTemplate.sendDefault(json).get();

        new CountDownLatch(1).await(3, TimeUnit.SECONDS);

        verify(libraryEventConsumer, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventService, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        Iterable<LibraryEntity> all = repository.findAll();
        BookEntity book = all.iterator().next().getBook();
        assertEquals(book.getName(), "meu-livro");
        assertNotNull(book.getId());

    }

    @Test
    public void shouldUpdateOnDatabase() throws ExecutionException, InterruptedException, JsonProcessingException {

        String json = "{\"libraryEventId\":null,\"type\":\"NEW\",\"book\":{\"id\":null,\"name\":\"um livro\"}}";
        LibraryEntity entity = new ObjectMapper().readValue(json, LibraryEntity.class);
        entity.getBook().setLibraryEntity(entity);
        LibraryEntity savedLibrary = repository.save(entity);

        savedLibrary.getBook().setName("Novo-Livro");
        savedLibrary.setType(LibraryEventType.UPDATE);

        kafkaTemplate.sendDefault(new ObjectMapper().writeValueAsString(savedLibrary));


        new CountDownLatch(1).await(3, TimeUnit.SECONDS);

        verify(libraryEventConsumer, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventService, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        Optional<LibraryEntity> expectedEvent = repository.findById(savedLibrary.getLibraryEventId());
        assertTrue(expectedEvent.isPresent());
        assertEquals(expectedEvent.get().getBook().getName(), savedLibrary.getBook().getName());

    }

    @Test
    public void shouldThrowIllegalWhenLibraryEntityIsNull() throws JsonProcessingException, InterruptedException {
        String json = "{\"libraryEventId\":9991,\"type\":\"UPDATE\",\"book\":{\"id\":null,\"name\":\"um livro\"}}";
        LibraryEntity entity = new ObjectMapper().readValue(json, LibraryEntity.class);

        kafkaTemplate.sendDefault(new ObjectMapper().writeValueAsString(entity));

        new CountDownLatch(1).await(3, TimeUnit.SECONDS);

        verify(libraryEventConsumer, times(3)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventService, times(3)).processLibraryEvent(isA(ConsumerRecord.class));
    }



}
