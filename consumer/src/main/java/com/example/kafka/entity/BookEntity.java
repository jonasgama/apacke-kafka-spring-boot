package com.example.kafka.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class BookEntity {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToOne
    @JoinColumn(name="libraryEventId")
    private LibraryEntity libraryEntity;
}
