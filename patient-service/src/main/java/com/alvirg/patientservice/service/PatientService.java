package com.alvirg.patientservice.service;

import com.alvirg.patientservice.dto.PatientRequest;
import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.exception.EmailAlreadyExistsException;
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

       var emailExists = this.repository.existsByEmail(request.getEmail());
       if(emailExists){
           throw new EmailAlreadyExistsException("A patient with this email already exists " + request.getEmail());
       }

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
