package com.copay.app.service;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.PasswordUpdateRequest;
import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(UserRegisterRequest request) {
    	
        // Check if username or email is already taken.
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create user entity.
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Encrypt password.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        // Save user to DB.
        userRepository.save(user);

        return "User registered successfully";
    }
    
    public ResponseEntity<?> updatePassword(Long id, PasswordUpdateRequest request, Principal principal) {
        
    	// Find user by ID.
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the authenticated user can change de password
        if (!user.getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own password.");
        }

        // Check if the password is correct.
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect.");
        }

        // Save password with hash.
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully.");
    }

}
