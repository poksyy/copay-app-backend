package com.copay.app.controller;

import com.copay.app.dto.responses.ResetPasswordResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.password.ForgotPasswordDTO;
import com.copay.app.dto.password.ForgotPasswordResetDTO;
import com.copay.app.dto.password.ResetPasswordDTO;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.EmailService;
import com.copay.app.service.JwtService;
import com.copay.app.service.password.PasswordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

    private final PasswordService passwordService;

    // Constructor
    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // Resets the user's password from within the app using a valid JWT token and current password.
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {

        // Extract the token from the Authorization header
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        // Delegate to the service

        return passwordService.resetPassword(resetPasswordDTO, token);

    }


    // Sends a password reset link to the user's email for the "Forgot Password" Login flow.
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO request) {

        return passwordService.forgotPassword(request);
    }

    // Resets the user's password via a token from the "Forgot Password" email link.
    @PutMapping("/forgot-password-reset")
    public ResponseEntity<?> forgotPasswordReset(@RequestBody @Valid ForgotPasswordResetDTO passwordResetRequest) {

        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        return passwordService.forgotPasswordReset(token, passwordResetRequest);
    }
}
