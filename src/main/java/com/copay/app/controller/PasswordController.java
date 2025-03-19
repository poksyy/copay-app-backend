package com.copay.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.password.ForgotPasswordRequest;
import com.copay.app.dto.password.ResetPasswordRequest;
import com.copay.app.service.EmailService;
import com.copay.app.service.JwtService;
import com.copay.app.service.password.PasswordService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

	@Autowired
	private PasswordService passwordService;
	private final EmailService emailService;
	private final JwtService jwtService;

	public PasswordController(EmailService emailService, JwtService jwtService) {
		this.emailService = emailService;
		this.jwtService = jwtService;
	}

	@PutMapping("/{id}/reset-password")
	public ResponseEntity<?> resetPassword(@PathVariable Long id,
			@RequestBody @Valid ResetPasswordRequest passwordUpdateRequest,
			@RequestHeader("Authorization") String authorizationHeader) {

		// Extract the token from the Authorization header
		String token = authorizationHeader.replace("Bearer ", "");

		return passwordService.resetPassword(id, passwordUpdateRequest, token);
	}

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();

        String token = jwtService.generateToken(email);

        // TODO implement useful url to update the password
        String resetLink = "" + token;

        try {
            emailService.sendResetPasswordEmail(email, resetLink);
            return ResponseEntity.ok("Email sent successfully.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error sending the email.");
        }
    }
}
