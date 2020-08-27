package com.example.kafka.service;

import com.example.kafka.document.LibraryEntity;
import com.example.kafka.document.LibraryEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class LibraryRecoveryService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public void handleRecovery(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {

        log.info("recovering record...");

        Integer key = consumerRecord.key();
        String value = consumerRecord.value();

        LibraryEntity entity = objectMapper.readValue(value, LibraryEntity.class);
        if(entity.getLibraryEventId()==null){
            entity.setType(LibraryEventType.NEW);
        }else{
            entity.setType(LibraryEventType.UPDATE);
        }
        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, objectMapper.writeValueAsString(entity)); //send to default topic
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
