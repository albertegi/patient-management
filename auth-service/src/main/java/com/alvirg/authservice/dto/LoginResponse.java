package com.alvirg.authservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
