package com.alvirg.patientservice.service;

import com.alvirg.patientservice.dto.PatientRequest;
import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.mapper.PatientMapper;
import com.alvirg.patientservice.model.Patient;
import com.alvirg.patientservice.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final PatientMapper mapper;

    public PatientResponse createPatient(PatientRequest request){

//        Patient patient = this.repository.save(this.mapper.toPatient(request));
//        return mapper.toPatientResponse(patient);
        return mapper.toPatientResponse(this.repository.save(this.mapper.toPatient(request)));

    }

    public List<PatientResponse> getPatients(){
        List<Patient> patients = repository.findAll();

        return patients
                .stream()
                .map(this.mapper::toPatientResponse)
                .toList();
    }

}
