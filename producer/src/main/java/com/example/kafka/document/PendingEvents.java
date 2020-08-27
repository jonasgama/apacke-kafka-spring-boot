package com.example.kafka.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "pendingbooks")
@Data
public class PendingEvents {

    @Id
    private String event;
    private String createdAt;

    public PendingEvents(){
    }
    public PendingEvents(String json){
        this.event = json;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
