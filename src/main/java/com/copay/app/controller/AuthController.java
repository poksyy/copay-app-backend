package com.copay.app.controller;

import com.copay.app.dto.JwtResponse;
import com.copay.app.dto.auth.UserLoginRequest;
import com.copay.app.dto.auth.UserRegisterStepOneDTO;
import com.copay.app.dto.auth.UserRegisterStepTwoDTO;
import com.copay.app.service.ValidationService;
import com.copay.app.service.auth.AuthService;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	// Handles user registration request.
	@PostMapping("/register/step-one")
	public ResponseEntity<?> registerStepOne(@RequestBody @Valid UserRegisterStepOneDTO userRegisterStepOneDTO,
			BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Registers the user and returns a JWT response.
		JwtResponse jwtResponse = authService.registerStepOne(userRegisterStepOneDTO);
		
		return ResponseEntity.ok().body(jwtResponse);
	}

	// Update phone number of the user.
	@PostMapping("/register/step-two")
	public ResponseEntity<?> registerStepTwo(@RequestBody @Valid UserRegisterStepTwoDTO userRegisterStepTwoDTO,
			BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Get the authentication thought the JwtAuthenticationFilter class.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		// Registers the user and returns a JWT response.
		JwtResponse jwtResponse = authService.registerStepTwo(userRegisterStepTwoDTO, token);

		return ResponseEntity.ok().body(jwtResponse);
	}

	// Handles user login request.
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginRequest loginRequest, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Authenticates the user and returns a JWT token.
		JwtResponse jwtToken = authService.loginUser(loginRequest);

		return ResponseEntity.ok(jwtToken);
	}
	
    // Handles user logout request.
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
		// Get the authentication thought the JwtAuthenticationFilter class.
    	String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
    	
        // Call the service to handle the token invalidation
        authService.logout(token);

        return ResponseEntity.ok("User logged out successfully");
    }
}
