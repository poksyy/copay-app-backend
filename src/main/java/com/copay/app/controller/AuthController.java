package com.copay.app.controller;

import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.service.UserService;
import com.copay.app.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        try {
            userService.registerUser(userRegisterRequest);

            // Generates a token for the specific username.
            String token = jwtService.generateToken(userRegisterRequest.getUsername());

            return ResponseEntity.ok().body("JWT Token: " + token);
            
        } catch (Exception e) {
        	// HTTP 400 -> BAD REQUEST.
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
