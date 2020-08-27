package com.example.kafka.repository;

import com.example.kafka.document.PendingEvents;
import com.example.kafka.domain.Book;
import com.example.kafka.event.LibraryEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PendingEventRepository extends MongoRepository<PendingEvents, Book>, CustomMongoRepository {
    LibraryEvent findLastCreated();
}
