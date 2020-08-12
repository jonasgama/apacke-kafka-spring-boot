package com.example.kafka.repository;

import com.example.kafka.entity.LibraryEntity;
import org.springframework.data.repository.CrudRepository;

public interface LibraryEventRepository extends CrudRepository<LibraryEntity, Integer> {
}
