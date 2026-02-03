package com.alvirg.patientservice.service;

import com.alvirg.patientservice.dto.PatientRequest;
import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.exception.EmailAlreadyExistsException;
import com.alvirg.patientservice.exception.PatientNotfoundException;
import com.alvirg.patientservice.mapper.PatientMapper;
import com.alvirg.patientservice.model.Patient;
import com.alvirg.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper mapper;

    public PatientResponse createPatient(PatientRequest patientRequest){
       var emailExists = this.patientRepository.existsByEmail(patientRequest.getEmail());
       if(emailExists){
           throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequest.getEmail());
       }
        return mapper.toPatientResponse(this.patientRepository.save(this.mapper.toPatient(patientRequest)));
    }

    public PatientResponse updatePatient(UUID id, PatientRequest patientRequest){
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotfoundException("Patient not found with ID: " + id));

        var emailExists = this.patientRepository.existsByEmail(patientRequest.getEmail());
        if(emailExists){
            throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequest.getEmail());
        }

        patient.setName(patientRequest.getName());
        patient.setAddress(patientRequest.getAddress());
        patient.setEmail(patientRequest.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequest.getDateOfBirth()));

        Patient updatedPatient = this.patientRepository.save(patient);
        return mapper.toPatientResponse(updatedPatient);


    }

    public List<PatientResponse> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients
                .stream()
                .map(this.mapper::toPatientResponse)
                .toList();
    }

}
