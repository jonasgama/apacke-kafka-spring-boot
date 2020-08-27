package com.example.kafka.repository;

import com.example.kafka.document.LibraryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Book;

public interface LibraryEventRepository extends CrudRepository<LibraryEntity, Integer> {

}
