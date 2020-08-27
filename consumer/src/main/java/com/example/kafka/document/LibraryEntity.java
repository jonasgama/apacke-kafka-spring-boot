package com.example.kafka.document;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="libraryEventId")
public class LibraryEntity {

    @Id
    @GeneratedValue
    private Integer libraryEventId;
    @Enumerated(EnumType.STRING)
    private LibraryEventType type;
    @OneToOne(mappedBy = "libraryEntity", cascade = {CascadeType.ALL})
    @ToString.Exclude //impede referencia circular
    private BookEntity book;

    public void updateName(BookEntity book){
        this.book.setName(book.getName());
        this.type = LibraryEventType.UPDATE;
    }
}
