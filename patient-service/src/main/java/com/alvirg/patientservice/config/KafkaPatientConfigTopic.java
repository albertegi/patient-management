package com.alvirg.patientservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaPatientConfigTopic {

    @Bean
    public NewTopic patientTopic(){
        return TopicBuilder
                .name("patient-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
