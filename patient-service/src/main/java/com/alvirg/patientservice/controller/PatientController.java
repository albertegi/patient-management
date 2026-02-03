package com.alvirg.patientservice.controller;

import com.alvirg.patientservice.dto.PatientRequest;
import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.dto.validators.CreatePatientValidationGroup;
import com.alvirg.patientservice.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Validated({Default.class, CreatePatientValidationGroup.class})
            @RequestBody
            PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.createPatient(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable("id")
            UUID id,
            @Validated({Default.class})
            @RequestBody
            PatientRequest patientRequest
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.service.updatePatient(id, patientRequest));
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getPatients() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.service.getPatients());
    }


}


