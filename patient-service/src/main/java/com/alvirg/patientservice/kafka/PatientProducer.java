package com.alvirg.patientservice.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientProducer {

    private final KafkaTemplate<String,byte[]> kafkaTemplate;
}
