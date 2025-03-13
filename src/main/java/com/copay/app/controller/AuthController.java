package com.copay.app.controller;

import com.copay.app.dto.JwtResponse;
import com.copay.app.dto.UserLoginRequest;
import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.service.UserService;
import com.copay.app.service.ValidationService;
import com.copay.app.service.auth.AuthService;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authenticationService;

    // Handles user registration request.
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest,
                                          BindingResult result) {
        // Validates user input.
        ValidationErrorResponse validationResponse = ValidationService.validate(result);
        
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        // Registers the user and returns a JWT response.
        JwtResponse jwtResponse = authenticationService.registerUser(userRegisterRequest);
        return ResponseEntity.ok().body(jwtResponse);
    }

    // Handles user login request.
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginRequest loginRequest,
                                          BindingResult result) {
        // Validates login input.
        ValidationErrorResponse validationResponse = ValidationService.validate(result);
        
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        // Authenticates the user and returns a JWT token.
        JwtResponse jwtToken = authenticationService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtToken);
    }
}

