package com.copay.app.controller;

import com.copay.app.dto.JwtResponse;
import com.copay.app.dto.UserLoginRequest;
import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.service.UserService;
import com.copay.app.service.ValidationService;
import com.copay.app.service.auth.AuthenticationService;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.validation.Valid;

import com.copay.app.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest,
			BindingResult result) {

		// Validate the request through the ValidationService.
		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		try {
			userService.registerUser(userRegisterRequest);

			// Generates a token for the specific user.
			JwtResponse jwtResponse = userService.registerUser(userRegisterRequest);

			return ResponseEntity.ok().body(jwtResponse);

		} catch (Exception e) {
			
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticate(@RequestBody @Valid UserLoginRequest loginRequest, BindingResult result) {

		// Validate the request through the ValidationService.
		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		try {

			// Authenticate the user and obtain the JWT token.
			JwtResponse jwtToken = authenticationService.authenticateUser(loginRequest);

			// Return the JWT token.
			return ResponseEntity.ok(jwtToken);

		} catch (RuntimeException e) {

			// If there is an error with authentication, return an error response.
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}

	}
}
