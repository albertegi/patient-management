package com.alvirg.patientservice.mapper;

import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class PatientMapper {

    public PatientResponse toPatientResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId().toString())
                .name(patient.getName())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .dateOfBirth(patient.getDateOfBirth().toString())
                .build();
    }
}

