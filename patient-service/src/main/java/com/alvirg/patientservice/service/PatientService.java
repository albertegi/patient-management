package com.alvirg.patientservice.service;

import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.mapper.PatientMapper;
import com.alvirg.patientservice.model.Patient;
import com.alvirg.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final PatientMapper mapper;

    public List<PatientResponse> getPatients(){
        List<Patient> patients = repository.findAll();

        return patients
                .stream()
                .map(this.mapper::toPatientResponse)
                .toList();
    }

}
