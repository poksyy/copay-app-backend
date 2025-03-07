package com.copay.app.controller;

import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.service.UserService;
import com.copay.app.service.ValidationService;
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
	private UserService userService;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest,
			BindingResult result) {

		// Validate the request and check for validation errors through the ValidationService.
		ValidationErrorResponse validationResponse = ValidationService.validate(result);
		
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		try {
			userService.registerUser(userRegisterRequest);

			// Generates a token for the specific user.
			String token = jwtService.generateToken(userRegisterRequest.getUsername());

			return ResponseEntity.ok().body("JWT Token: " + token);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}
}
