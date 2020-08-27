package com.example.kafka.producer;

import com.example.kafka.document.PendingEvents;
import com.example.kafka.event.LibraryEvent;
import com.example.kafka.repository.PendingEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private PendingEventRepository pendings;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        String record = objMapper.writeValueAsString(libraryEvent);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = this.sentRecord(libraryEvent, record);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error Sending the Message and the exception is {}", ex.getMessage());
                try {
                    throw ex;
                } catch (Throwable throwable) {
                    log.error("Error in OnFailure: {}", throwable.getMessage());
                    pendings.save(new PendingEvents(record));
                }
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                log.info(result.toString());
            }
        });
    }

    public void sendLibraryPendingEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        String record = objMapper.writeValueAsString(libraryEvent);
        ListenableFuture<SendResult<Integer, String>> listenableFuture = this.sentRecord(libraryEvent, record);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Pending event has failed after recovery: {}, cause: {}", record ,ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                log.info("record has been recovered from pending database: {}", result.toString());
                pendings.delete(new PendingEvents(record));
            }
        });
    }

    private ListenableFuture<SendResult<Integer, String>> sentRecord(LibraryEvent libraryEvent, String record) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        //headers
        List<Header> headers = Arrays.asList(new RecordHeader("event-source", "scanner".getBytes()));
        ProducerRecord<Integer, String> event = new ProducerRecord<>("library-events", null, key, record, headers);

        return kafkaTemplate.send(event);
    }
}
