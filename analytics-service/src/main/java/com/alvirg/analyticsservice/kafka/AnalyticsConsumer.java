package com.alvirg.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsConsumer {

    @KafkaListener(topics = "patient-topic", groupId = "${spring.kafka.consumer.group-id:analytics-service}")
    public void consumeEvent(byte[] payload) {
        if (payload == null || payload.length == 0) return;
        try {
            log.info("Consuming message from patient-topic, size={}", payload.length);
            PatientEvent patientEvent = PatientEvent.parseFrom(payload);
            log.info("Received Patient Event: [PatientId={}, PatientName={}, PatientEmail={}]",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event: {}", e.getMessage());
        } catch (Throwable t) {
            log.error("Unexpected error in consumeEvent", t);
        }
    }
}
