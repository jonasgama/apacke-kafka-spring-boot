package com.example.kafka.repository;

import com.example.kafka.event.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CustomMongoRepository {
    LibraryEvent findLastCreated() throws JsonProcessingException;
}
