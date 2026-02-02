package com.alvirg.patientservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    private String id;
    private String name;
    private String email;
    private String address;
    private String dateOfBirth;
}
