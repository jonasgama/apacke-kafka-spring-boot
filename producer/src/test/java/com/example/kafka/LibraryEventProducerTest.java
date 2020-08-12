package com.example.kafka;

import com.example.kafka.domain.Book;
import com.example.kafka.event.LibraryEvent;
import com.example.kafka.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objMapper;

    @InjectMocks
    LibraryEventProducer producer;

    @Test
    public void onFailure() throws JsonProcessingException {


        LibraryEvent senhorDosAneis = LibraryEvent.builder()
                .book(Book.builder()
                        .id(1001)
                        .name("senhor dos anéis")
                        .build())
                .build();


        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Kafka is Out!"));

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        producer.sendLibraryEvent(senhorDosAneis);

        assertThrows(RuntimeException.class, () -> kafkaTemplate.send(any(ProducerRecord.class)).get());

    }

    @Test
    public void onSuccess() throws JsonProcessingException {


        LibraryEvent senhorDosAneis = LibraryEvent.builder()
                .book(Book.builder()
                        .id(1001)
                        .name("senhor dos anéis")
                        .build())
                .build();


        SettableListenableFuture future = new SettableListenableFuture();

        RecordMetadata record = new RecordMetadata(new TopicPartition("library-events", 0),1,1, System.currentTimeMillis(), 1l,1,1);
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>("library-events", senhorDosAneis.getLibraryEventId(), objMapper.writeValueAsString(senhorDosAneis));
        SendResult<Integer, String> send = new SendResult<>(producerRecord, record);
        future.set(send);

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        producer.sendLibraryEvent(senhorDosAneis);


    }



}
