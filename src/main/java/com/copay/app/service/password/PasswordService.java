package com.copay.app.service.password;

import com.copay.app.dto.password.*;
import com.copay.app.dto.responses.ResetPasswordResponseDTO;
import com.copay.app.entity.User;
import com.copay.app.exception.*;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.EmailService;
import com.copay.app.service.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public PasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(ResetPasswordDTO request, String token) {

            String phoneNumberToken = jwtService.getUserIdentifierFromToken(token);

            // Find user by email through the temporal token.
            User user = userRepository.findByPhoneNumber(phoneNumberToken)
                    .orElseThrow(() -> new UserNotFoundException("User not found" ));

            // Check if the current password is correct.
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IncorrectPasswordException("Current password is incorrect.");
            }

            // Save the new password with hash
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));

            userRepository.save(user);

            return ResponseEntity.ok(new ResetPasswordResponseDTO("Password updated successfully."));

    }

    public ResponseEntity<?> forgotPassword(ForgotPasswordDTO request) {

        boolean emailExists = userRepository.existsByEmail(request.getEmail());

        // Verify if the email exists or not.
        if (!emailExists) {
            throw new EmailAlreadyExistsException("Email <" + request.getEmail() + "> does not exist.");
        }

        // Generate JWT token for the password reset.
        String jwtToken = jwtService.generateTemporaryToken(request.getEmail());

        // Generate reset link.
        String resetLink = "http://localhost:8080/reset-password.html?token=" + jwtToken;
        String email = request.getEmail();

        try {
            emailService.sendResetPasswordEmail(email, resetLink);

            // Create response object with message and email
            ForgotPasswordResponseDTO responseDTO = new ForgotPasswordResponseDTO();
            responseDTO.setMessage("Password reset email sent successfully.");
            responseDTO.setEmail(email);
            responseDTO.setToken(jwtToken);

            return ResponseEntity.ok(responseDTO);

        } catch (MessagingException e) {
            throw new EmailSendingException("Error sending the email. " + e);
        }
    }

    public ResponseEntity<?> forgotPasswordReset(String token, ForgotPasswordResetDTO request) {

        String email = jwtService.getUserIdentifierFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update the password.
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Create response object with message and email
        ForgotPasswordResetResponseDTO response = new ForgotPasswordResetResponseDTO();
        response.setMessage("Password reset updated successfully.");
        response.setEmail(email);

        return ResponseEntity.ok(response);
    }
}