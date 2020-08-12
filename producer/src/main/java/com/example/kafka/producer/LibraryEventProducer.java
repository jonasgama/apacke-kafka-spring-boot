package com.example.kafka.producer;

import com.example.kafka.event.LibraryEvent;
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

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objMapper.writeValueAsString(libraryEvent);

        //headers
        List<Header> headers = Arrays.asList(new RecordHeader("event-source", "scanner".getBytes()));
        ProducerRecord<Integer, String> record = new ProducerRecord<>("library-events", null, key, value, headers);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(record);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error Sending the Message and the exception is {}", ex.getMessage());
                try {
                    throw ex;
                } catch (Throwable throwable) {
                    log.error("Error in OnFailure: {}", throwable.getMessage());
                }
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                log.info(result.toString());
            }
        });
    }
}
