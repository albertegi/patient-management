package com.alvirg.patientservice.mapper;

import com.alvirg.patientservice.dto.PatientRequest;
import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public Patient toPatient(PatientRequest request) {
        return Patient.builder()
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth()))
                .build();
    }
}

