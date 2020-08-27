package com.example.kafka.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class AutoCreateConfig {

    @Bean
    public NewTopic libraryTopic(){
        return TopicBuilder.name("library-events")
                .partitions(4)
                .replicas(2)
                .build();
    }

}
