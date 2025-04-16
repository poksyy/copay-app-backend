package com.copay.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.password.ForgotPasswordDTO;
import com.copay.app.dto.password.ForgotPasswordResetDTO;
import com.copay.app.dto.password.ResetPasswordDTO;
import com.copay.app.service.password.PasswordServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

    private final PasswordServiceImpl passwordServiceImpl;

    // Constructor
    public PasswordController(PasswordServiceImpl passwordServiceImpl) {
        this.passwordServiceImpl = passwordServiceImpl;
    }

    // Resets the user's password from within the app using a valid JWT token and current password.
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {

        // Extract the token from the Authorization header
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        // Delegate to the service

        return passwordServiceImpl.resetPassword(resetPasswordDTO, token);

    }


    // Sends a password reset link to the user's email for the "Forgot Password" Login flow.
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO request) {

        return passwordServiceImpl.forgotPassword(request);
    }

    // Resets the user's password via a token from the "Forgot Password" email link.
    @PutMapping("/forgot-password-reset")
    public ResponseEntity<?> forgotPasswordReset(@RequestBody @Valid ForgotPasswordResetDTO passwordResetRequest) {

        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        return passwordServiceImpl.forgotPasswordReset(token, passwordResetRequest);
    }
}
