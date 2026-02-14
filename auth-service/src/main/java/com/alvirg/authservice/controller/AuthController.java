package com.alvirg.authservice.controller;

import com.alvirg.authservice.AuthServiceApplication;
import com.alvirg.authservice.dto.LoginRequest;
import com.alvirg.authservice.dto.LoginResponse;
import com.alvirg.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
            ){

        Optional<String> tokenOptional = authService.authenticate(loginRequest);

        if(tokenOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // the get converts the tokenOptional token into an actual string
        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponse(token));

    }
}
