package com.example.kafka.rest;

import com.example.kafka.domain.Book;
import com.example.kafka.event.LibraryEvent;
import com.example.kafka.event.LibraryEventType;
import com.example.kafka.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;

@RestController
public class BookController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;


    @PostMapping("/v1/book")
    public ResponseEntity<Book> post(@RequestBody @Valid Book book) throws JsonProcessingException {

        libraryEventProducer.sendLibraryEvent(LibraryEvent.builder()
                .book(book)
                .type(LibraryEventType.NEW)
                .build());
        return ResponseEntity.accepted().build();
    }


    @PutMapping("/v1/book/{id}")
    public ResponseEntity<Book> put(@PathVariable @NotBlank Integer id, @RequestBody @Valid Book book) throws JsonProcessingException {
        libraryEventProducer.sendLibraryEvent(LibraryEvent.builder()
                .libraryEventId(id)
                .type(LibraryEventType.UPDATE)
                .book(book)
                .build());
        return ResponseEntity.accepted().build();
    }


}
