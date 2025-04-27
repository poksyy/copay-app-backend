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

import com.copay.app.dto.user.response.profile.EmailResponseDTO;
import com.copay.app.dto.user.response.profile.PhoneNumberResponseDTO;
import com.copay.app.dto.user.response.profile.UsernameResponseDTO;
import com.copay.app.dto.user.request.UserCreateDTO;
import com.copay.app.dto.user.request.UserDeleteDTO;
import com.copay.app.dto.user.response.UserResponseDTO;
import com.copay.app.dto.user.request.UserUpdateDTO;
import com.copay.app.dto.user.request.profile.UpdateEmailDTO;
import com.copay.app.dto.user.request.profile.UpdatePhoneNumberDTO;
import com.copay.app.dto.user.request.profile.UpdateUsernameDTO;
import com.copay.app.service.ValidationService;
import com.copay.app.service.user.UserServiceImpl;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserServiceImpl userServiceImpl;

	// Constructor
	public UserController(UserServiceImpl userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}

	// Handles user creation with validation.
	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO request) {

		// Convert the saved entity to a response DTO.
		UserResponseDTO response = userServiceImpl.createUser(request);

		return ResponseEntity.ok().body(response);
	}

	// Retrieves a user by their ID and returns it as a DTO.
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

		UserResponseDTO response = userServiceImpl.getUserByIdDTO(id);

		return ResponseEntity.ok(response);
	}

	// Retrieves a user by their phone number and returns it as a DTO.
	@GetMapping("/phone/{phoneNumber}")
	public ResponseEntity<UserResponseDTO> getUserByPhone(@PathVariable String phoneNumber) {

		UserResponseDTO response = userServiceImpl.getUserByPhoneDTO(phoneNumber);

		return ResponseEntity.ok(response);
	}

	// Updates a user with the provided ID.
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO request) {

		// Delegate the update to the service.
		UserResponseDTO response = userServiceImpl.updateUser(id, request);

		return ResponseEntity.ok(response);
	}

	// Updates a user with the provided email.
	@PutMapping("/email/{email}")
	public ResponseEntity<?> updateUserByEmail(@PathVariable String email,
			@Valid @RequestBody UserUpdateDTO request) {

		// Convert the updated entity to a response DTO.
		UserResponseDTO response = userServiceImpl.updateUser(email, request);

		return ResponseEntity.ok(response);
	}

	// Deletes a user by their ID and returns a response DTO (Used in the Controller)
	@DeleteMapping("/{id}")
	public ResponseEntity<UserDeleteDTO> deleteUser(@PathVariable Long id) {

		UserDeleteDTO responseDTO = userServiceImpl.deleteUser(id);

		return ResponseEntity.ok(responseDTO);
	}

	// Retrieves all users.
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

		List<UserResponseDTO> userResponseDTOs = userServiceImpl.getAllUsers();

		return ResponseEntity.ok(userResponseDTOs);
	}
	
	// Updates only the username of a user by ID.
	@PutMapping("/edit-username/{id}")
	public ResponseEntity<?> updateUsername(@PathVariable Long id, @Valid @RequestBody UpdateUsernameDTO request) {

	    // Delegate the username update to the service.
	    UsernameResponseDTO response = userServiceImpl.updateUsername(id, request);

	    return ResponseEntity.ok(response);
	}
	
	// Updates only the phone number of a user by ID.
	@PutMapping("/edit-phone/{id}")
	public ResponseEntity<?> updatePhoneNumber(@PathVariable Long id, @Valid @RequestBody UpdatePhoneNumberDTO request) {

	    // Delegate the phone number update to the service.
	    PhoneNumberResponseDTO response = userServiceImpl.updatePhoneNumber(id, request);

	    return ResponseEntity.ok(response);
	}

	// Updates only the email of a user by ID.
	@PutMapping("/edit-email/{id}")
	public ResponseEntity<?> updateEmail(@PathVariable Long id, @Valid @RequestBody UpdateEmailDTO request) {

		// Delegate the email update to the service.
		EmailResponseDTO response = userServiceImpl.updateEmail(id, request);

		return ResponseEntity.ok(response);
	}

}