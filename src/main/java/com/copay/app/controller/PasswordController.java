package com.copay.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.password.ForgotPasswordRequest;
import com.copay.app.dto.password.ForgotPasswordResetRequest;
import com.copay.app.dto.password.ResetPasswordRequest;
import com.copay.app.entity.User;
import com.copay.app.exception.UserNotFoundException;
import com.copay.app.repository.UserRepository;
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
            @RequestBody @Valid ResetPasswordRequest passwordUpdateRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "");

        // Delegate to the service
        return passwordService.resetPassword(id, passwordUpdateRequest, token);
    }

    // Sends a password reset link to the user's email for the "Forgot Password" Login flow.
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();

        // Delegate to the service
        return passwordService.forgotPassword(email);
    }

    // Resets the user's password via a token from the "Forgot Password" email link.
    @PutMapping("/forgot-password-reset")
    public ResponseEntity<?> forgotPasswordReset(@RequestBody @Valid ForgotPasswordResetRequest passwordResetRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "");

        // Delegate to the service
        return passwordService.forgotPasswordReset(token, passwordResetRequest);
    }
}
