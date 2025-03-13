package com.copay.app.service;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.PasswordUpdateRequest;
import com.copay.app.entity.User;
import com.copay.app.exception.EmailAlreadyExistsException;
import com.copay.app.exception.IncorrectPasswordException;
import com.copay.app.exception.PhoneAlreadyExistsException;
import com.copay.app.exception.UserPermissionException;
import com.copay.app.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {

        // Verify if the phone number exists or not.
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new PhoneAlreadyExistsException("Phone number already exists.");
        }
        
        // Verify if the phone number exists or not.
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        // Encode the password.
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Persists the user on the database.
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User getUserByPhone(String phoneNumber) {
    	
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("Phone number not found"));
    }

    public User updateUser(Long id, User userData) {
    	
        User user = getUserById(id);
        user.setUsername(userData.getUsername());
        user.setEmail(userData.getEmail());
        user.setPhoneNumber(userData.getPhoneNumber());
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
    	
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
    	
        return userRepository.findAll();
    }
}
