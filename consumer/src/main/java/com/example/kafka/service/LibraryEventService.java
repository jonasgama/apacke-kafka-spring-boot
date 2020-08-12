package com.example.kafka.service;

import com.example.kafka.entity.LibraryEntity;
import com.example.kafka.repository.LibraryEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LibraryEventService {

     @Autowired
     private ObjectMapper objectMapper;

     @Autowired
     private LibraryEventRepository repository;

     public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
         LibraryEntity entity = objectMapper.readValue(consumerRecord.value(), LibraryEntity.class);

         switch (entity.getType()){
             case NEW:
                 save(entity);
                 break;
             case UPDATE:
                 update(entity);
                break;
         }


     }

     private void save(LibraryEntity entity){
         entity.getBook().setLibraryEntity(entity);
         repository.save(entity);
     }
    private void update(LibraryEntity entity){
         //lookup for the event already saved
        Optional<LibraryEntity> byId = repository.findById(entity.getLibraryEventId());
        //in case of it is NOT saved, interrupt the flow
        if(!byId.isPresent()){
            throw new IllegalArgumentException("LibraryEntity do not exists");
        }
        //otherwise i will update the name of its book and set the typeEvent to Update
        byId.get().updateName(entity.getBook());
        //by the end, my event will update the book child associated (R 1x1)
        repository.save(byId.get());
    }

}
