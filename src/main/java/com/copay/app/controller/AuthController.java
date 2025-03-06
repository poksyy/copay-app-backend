package com.copay.app.controller;

import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.service.UserService;

import jakarta.validation.Valid;

import com.copay.app.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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

		// Check if there are validation errors.
		if (result.hasErrors()) {
		    // Create a StringBuilder to concatenate error messages.
		    StringBuilder errorMessages = new StringBuilder("Errors: ");
		    
		    // Iterate over all errors and append their messages.
		    for (ObjectError error : result.getAllErrors()) {
		        errorMessages.append(error.getDefaultMessage()).append(" ");
		    }
		    
		    // Return all error messages in the response.
		    return ResponseEntity.badRequest().body(errorMessages.toString());
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
