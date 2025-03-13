package com.copay.app.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.dto.UserResponseDTO;
import com.copay.app.entity.User;
import com.copay.app.service.UserService;
import com.copay.app.service.ValidationService;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Handles user creation with validation.
    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest, 
            BindingResult result) {

        // If validation errors exist, handle them with ValidationService.
        ValidationErrorResponse validationResponse = ValidationService.validate(result);
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        // Create user entity and set properties.
        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(userRegisterRequest.getPassword());
        user.setPhoneNumber(userRegisterRequest.getPhoneNumber());  // Assign phone number.
        user.setCreatedAt(LocalDateTime.now());  // Set creation date.

        // Save the user to the database.
        User savedUser = userService.createUser(user);

        // Convert the saved entity to a response DTO.
        UserResponseDTO responseDTO = new UserResponseDTO(savedUser);
        return ResponseEntity.ok(responseDTO);
    }

    // Retrieves a user by their ID.
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserResponseDTO responseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok(responseDTO);
    }

    // Retrieves a user by their phone number.
    @GetMapping("/{phone}")
    public ResponseEntity<UserResponseDTO> getUserByPhone(@PathVariable String phone) {
        User user = userService.getUserByPhone(phone);
        UserResponseDTO responseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok(responseDTO);
    }

    // Updates a user with the provided ID.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserRegisterRequest userRegisterRequest, 
            BindingResult result) {

        // If validation errors exist, handle them with ValidationService.
        ValidationErrorResponse validationResponse = ValidationService.validate(result);
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        // Find the existing user.
        User existingUser = userService.getUserById(id);

        // Update the user fields with the new data.
        existingUser.setUsername(userRegisterRequest.getUsername());
        existingUser.setEmail(userRegisterRequest.getEmail());
        existingUser.setPassword(userRegisterRequest.getPassword());
        existingUser.setPhoneNumber(userRegisterRequest.getPhoneNumber());  // Update phone number.
        existingUser.setCreatedAt(existingUser.getCreatedAt());  // Keep the creation date.

        // Save the updated user.
        User updatedUser = userService.updateUser(id, existingUser);

        // Convert the updated entity to a response DTO.
        UserResponseDTO responseDTO = new UserResponseDTO(updatedUser);
        return ResponseEntity.ok(responseDTO);
    }

    // Deletes a user by their ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Retrieves all users.
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> responseDTOs = users.stream()
                                                  .map(UserResponseDTO::new)
                                                  .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}