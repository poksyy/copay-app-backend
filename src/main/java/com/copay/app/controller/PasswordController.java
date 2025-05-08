package com.copay.app.controller;

import com.copay.app.service.password.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.password.request.ForgotPasswordRequestDTO;
import com.copay.app.dto.password.request.ForgotPasswordResetRequestDTO;
import com.copay.app.dto.password.request.ResetPasswordRequestDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

    private final PasswordService passwordService;

    // Constructor.
    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // Resets the user's password from within the app using a valid JWT token and current password.
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO) {

        // Extract the token from the Authorization header
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        return passwordService.resetPassword(resetPasswordRequestDTO, token);
    }

    // Sends a password reset link to the user's email for the "Forgot Password" Login flow.
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {

        return passwordService.forgotPassword(request);
    }

    // Resets the user's password via a token from the "Forgot Password" email link.
    @PutMapping("/forgot-password-reset")
    public ResponseEntity<?> forgotPasswordReset(@RequestBody @Valid ForgotPasswordResetRequestDTO passwordResetRequest) {

        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        return passwordService.forgotPasswordReset(token, passwordResetRequest);
    }
}
