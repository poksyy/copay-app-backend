package com.copay.app.controller;

import java.util.List;

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

import com.copay.app.dto.user.UserCreateDTO;
import com.copay.app.dto.user.UserDeleteDTO;
import com.copay.app.dto.user.UserResponseDTO;
import com.copay.app.dto.user.UserUpdateDTO;
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
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Convert the saved entity to a response DTO.
		UserResponseDTO userResponseDTO = userService.createUser(userCreateDTO);

		return ResponseEntity.ok().body(userResponseDTO);
	}

	// Retrieves a user by their ID and returns it as a DTO.
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

		UserResponseDTO userResponseDTO = userService.getUserByIdDTO(id);

		return ResponseEntity.ok(userResponseDTO);
	}

	// Retrieves a user by their phone number and returns it as a DTO.
	@GetMapping("/phone/{phoneNumber}")
	public ResponseEntity<UserResponseDTO> getUserByPhone(@PathVariable String phoneNumber) {

		UserResponseDTO userResponseDTO = userService.getUserByPhoneDTO(phoneNumber);

		return ResponseEntity.ok(userResponseDTO);
	}

	// Updates a user with the provided ID.
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO,
			BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Delegate the update to the service.
		UserResponseDTO userResponseDTO = userService.updateUser(id, userUpdateDTO);

		return ResponseEntity.ok(userResponseDTO);
	}

	// Updates a user with the provided email.
	@PutMapping("/email/{email}")
	public ResponseEntity<?> updateUserByEmail(@PathVariable String email,
			@Valid @RequestBody UserUpdateDTO userUpdateDTO, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Convert the updated entity to a response DTO.
		UserResponseDTO userResponseDTO = userService.updateUser(email, userUpdateDTO);

		return ResponseEntity.ok(userResponseDTO);
	}

	// Deletes a user by their ID and returns a response DTO (Used in the Controller)
	@DeleteMapping("/{id}")
	public ResponseEntity<UserDeleteDTO> deleteUser(@PathVariable Long id) {

		UserDeleteDTO responseDTO = userService.deleteUser(id);

		return ResponseEntity.ok(responseDTO);
	}

	// Retrieves all users.
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

		List<UserResponseDTO> userResponseDTOs = userService.getAllUsers();

		return ResponseEntity.ok(userResponseDTOs);
	}
}