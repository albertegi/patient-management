package com.alvirg.authservice.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private final String token;
}
