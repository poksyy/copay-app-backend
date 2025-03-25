package com.copay.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.JwtResponse;
import com.copay.app.dto.password.ForgotPasswordDTO;
import com.copay.app.dto.password.ForgotPasswordResetDTO;
import com.copay.app.dto.password.ForgotPasswordResponseDTO;
import com.copay.app.dto.password.ResetPasswordDTO;
import com.copay.app.entity.User;
import com.copay.app.exception.UserNotFoundException;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.EmailService;
import com.copay.app.service.JwtService;
import com.copay.app.service.ValidationService;
import com.copay.app.service.password.PasswordService;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;
    private EmailService emailService;
    private JwtService jwtService;
    private UserRepository userRepository;

    public PasswordController(EmailService emailService, JwtService jwtService, UserRepository userRepository) {
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // Resets the user's password from within the app using a valid JWT token and current password.
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id,
            @RequestBody @Valid ResetPasswordDTO request,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "");

        // Delegate to the service
        return passwordService.resetPassword(id, request, token);
    }

    // Sends a password reset link to the user's email for the "Forgot Password" Login flow.
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO request, BindingResult result) {
    	
		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}
		
		ResponseEntity<?> response = passwordService.forgotPassword(request);
        
        return response;
    }

    // Resets the user's password via a token from the "Forgot Password" email link.
    @PutMapping("/forgot-password-reset")
    public ResponseEntity<?> forgotPasswordReset(@RequestBody @Valid ForgotPasswordResetDTO passwordResetRequest,
            @RequestHeader("Authorization") String authorizationHeader, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}
		
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		// Registers the user and returns a JWT response.
		ResponseEntity<?> response = passwordService.forgotPasswordReset(token, passwordResetRequest);

		return response;
    }
}
