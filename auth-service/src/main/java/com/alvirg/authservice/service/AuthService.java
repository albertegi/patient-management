package com.alvirg.authservice.service;

import com.alvirg.authservice.dto.LoginRequest;
import com.alvirg.authservice.model.User;
import com.alvirg.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public Optional<String> authenticate(LoginRequest loginRequest){
        // first thing: get the user from the database
        Optional<String> token = userService
                .findByEmail(loginRequest.getEmail())
                .filter(u-> passwordEncoder.matches(loginRequest.getPassword(), u.getPassword()))
                .map(u-> jwtUtil.generateToken(u.getEmail(), u.getRole()));

        return token;

    }

    public boolean validateToken(String token) {

        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
