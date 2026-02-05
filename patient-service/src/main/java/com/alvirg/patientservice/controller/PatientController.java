package com.alvirg.patientservice.controller;

import com.alvirg.patientservice.dto.PatientRequest;
import com.alvirg.patientservice.dto.PatientResponse;
import com.alvirg.patientservice.dto.validators.CreatePatientValidationGroup;
import com.alvirg.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Patient", description = "API for managing Patients")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "Create a new Patient")
    public ResponseEntity<PatientResponse> createPatient(
            @Validated({Default.class, CreatePatientValidationGroup.class})
            @RequestBody
            PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.patientService.createPatient(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a new Patient")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable("id")
            UUID id,
            @Validated({Default.class})
            @RequestBody
            PatientRequest patientRequest
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.patientService.updatePatient(id, patientRequest));
    }

    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponse>> getPatients() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.patientService.getPatients());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Patient")
    public ResponseEntity<Void> deletePatient(
            @PathVariable("id")
            UUID id
    ){
        this.patientService.deletePatient(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}


