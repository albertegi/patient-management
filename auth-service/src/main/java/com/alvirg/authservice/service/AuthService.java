package com.alvirg.authservice.service;

import com.alvirg.authservice.dto.LoginRequest;
import com.alvirg.authservice.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    public Optional<String> authenticate(LoginRequest loginRequest){
        // first thing: get the user from the database
        Optional<User> user = userService.findByEmail(loginRequest.getEmail());

    }
}
