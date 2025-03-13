package com.copay.app.service.password;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.PasswordUpdateRequest;
import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;

@Service
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> updatePassword(Long id, PasswordUpdateRequest request, String token) {

    	String phoneNumber = jwtService.extractPhoneNumber(token);

        // Find user by ID.
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the authenticated user can change the password
        if (!user.getPhoneNumber().equals(phoneNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own password.");
        }

        // Check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect.");
        }

        // Save new password with hash
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully.");
    }

}
