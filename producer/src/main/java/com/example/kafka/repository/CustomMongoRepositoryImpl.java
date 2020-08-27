package com.example.kafka.repository;

import com.example.kafka.document.PendingEvents;
import com.example.kafka.event.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


public class CustomMongoRepositoryImpl implements CustomMongoRepository {

    @Autowired
    private MongoOperations mongoOp;

    @Autowired
    private ObjectMapper obj;

    public LibraryEvent findLastCreated() throws JsonProcessingException {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .limit(1);
        List<PendingEvents> pendingEvents = mongoOp.find(query, PendingEvents.class);
        if(pendingEvents.isEmpty()){
            return null;
        }
        PendingEvents pendingEvent = pendingEvents.get(0);
        return obj.readValue(pendingEvent.getEvent(), LibraryEvent.class);
    }
}
