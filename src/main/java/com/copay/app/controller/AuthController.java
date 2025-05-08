package com.copay.app.controller;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.auth.request.UserLoginRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepOneRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepTwoRequestDTO;
import com.copay.app.dto.auth.response.RegisterStepOneResponseDTO;
import com.copay.app.dto.auth.response.RegisterStepTwoResponseDTO;
import com.copay.app.dto.auth.response.LoginResponseDTO;
import com.copay.app.service.auth.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	// Constructor.
	public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Handles user registration request.
	@PostMapping("/register/step-one")
	public ResponseEntity<?> registerStepOne(@RequestBody @Valid UserRegisterStepOneRequestDTO userRegisterStepOneRequestDTO) {

		// Registers the user and returns a JWT response.
		RegisterStepOneResponseDTO registerStepOneResponseDTO = authService.registerStepOne(userRegisterStepOneRequestDTO);
		
		return ResponseEntity.ok().body(registerStepOneResponseDTO);
	}

	// Update phone number of the user.
	@PostMapping("/register/step-two")
	public ResponseEntity<?> registerStepTwo(@RequestBody @Valid UserRegisterStepTwoRequestDTO userRegisterStepTwoRequestDTO) {

		// Get the authentication though the JwtAuthenticationFilter class.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		// Registers the user and returns a JWT response.
		RegisterStepTwoResponseDTO registerStepTwoResponseDTO = authService.registerStepTwo(userRegisterStepTwoRequestDTO, token);

		return ResponseEntity.ok().body(registerStepTwoResponseDTO);
	}

	// Handles user login request.
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginRequestDTO loginRequest) {

		// Authenticates the user and returns a JWT token.
		LoginResponseDTO loginResponseDTO = authService.loginUser(loginRequest);

		return ResponseEntity.ok(loginResponseDTO);
  	}

	// Handles user logout request.
	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser() {

		// Get the token from the SecurityContextHolder (set by JwtAuthenticationFilter)
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		// Call the service to handle the token invalidation
		authService.logout(token);

		return ResponseEntity.ok(new MessageResponseDTO("Logout successful."));
	}

}
