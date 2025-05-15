package com.copay.app.controller;

import java.util.List;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.user.request.*;
import com.copay.app.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.copay.app.dto.user.response.profile.EmailResponseDTO;
import com.copay.app.dto.user.response.profile.PhoneNumberResponseDTO;
import com.copay.app.dto.user.response.profile.UsernameResponseDTO;
import com.copay.app.dto.user.response.UserResponseDTO;
import com.copay.app.dto.user.request.profile.UpdateEmailRequestDTO;
import com.copay.app.dto.user.request.profile.UpdatePhoneNumberRequestDTO;
import com.copay.app.dto.user.request.profile.UpdateUsernameRequestDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	// Constructor.
	public UserController(UserService userService) {
		this.userService = userService;
	}

	// Handles user creation with validation.
	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequestDTO) {

		UserResponseDTO userResponseDTO = userService.createUser(createUserRequestDTO);

		return ResponseEntity.ok().body(userResponseDTO);
	}

	// Retrieves a user by their ID and returns it as a DTO.
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id, @Valid @ModelAttribute GetUserByIdRequestDTO getUserByIdRequestDTO) {

		// The userId is manually added to the DTO for validation.
		getUserByIdRequestDTO.setUserId(id);

		UserResponseDTO userResponseDTO = userService.getUserByIdDTO(id);

		return ResponseEntity.ok(userResponseDTO);
	}

	// Retrieves a user by their phone number and returns it as a DTO.
	@GetMapping("/phone/{phoneNumber}")
	public ResponseEntity<UserResponseDTO> getUserByPhone(@PathVariable String phoneNumber, @Valid @ModelAttribute GetUserByPhoneRequestDTO getUserByPhoneRequestDTO) {

		// The phoneNumber is manually added to the DTO for validation.
		getUserByPhoneRequestDTO.setPhoneNumber(phoneNumber);

		UserResponseDTO userResponseDTO = userService.getUserByPhoneDTO(phoneNumber);

		return ResponseEntity.ok(userResponseDTO);
	}

	// Updates a user with the provided ID.
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {

		UserResponseDTO userResponseDTO = userService.updateUser(id, updateUserRequestDTO);

		return ResponseEntity.ok(userResponseDTO);
	}

	// Deletes a user by their ID and returns a response DTO (Used in the Controller).
	@DeleteMapping("/{id}")
	public ResponseEntity<MessageResponseDTO> deleteUser(@PathVariable Long id, @Valid @ModelAttribute DeleteUserRequestDTO deleteUserRequestDTO) {

		// The userId is manually added to the DTO for validation.
		deleteUserRequestDTO.setUserId(id);

		MessageResponseDTO messageResponseDTO = userService.deleteUser(id);

		return ResponseEntity.ok(messageResponseDTO);
	}

	// Retrieves all users.
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

		List<UserResponseDTO> userResponseDTOs = userService.getAllUsers();

		return ResponseEntity.ok(userResponseDTOs);
	}
	
	// Updates only the username of a user by ID.
	@PutMapping("/edit-username/{id}")
	public ResponseEntity<?> updateUsername(@PathVariable Long id, @Valid @RequestBody UpdateUsernameRequestDTO updateUsernameRequestDTO) {

	    // Delegate the username update to the service.
	    UsernameResponseDTO response = userService.updateUsername(id, updateUsernameRequestDTO);

	    return ResponseEntity.ok(response);
	}
	
	// Updates only the phone number of a user by ID.
	@PutMapping("/edit-phone/{id}")
	public ResponseEntity<?> updatePhoneNumber(@PathVariable Long id, @Valid @RequestBody UpdatePhoneNumberRequestDTO updatePhoneNumberRequestDTO) {

	    // Delegate the phone number update to the service.
	    PhoneNumberResponseDTO response = userService.updatePhoneNumber(id, updatePhoneNumberRequestDTO);

	    return ResponseEntity.ok(response);
	}

	// Updates only the email of a user by ID.
	@PutMapping("/edit-email/{id}")
	public ResponseEntity<?> updateEmail(@PathVariable Long id, @Valid @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO) {

		// Delegate the email update to the service.
		EmailResponseDTO response = userService.updateEmail(id, updateEmailRequestDTO);

		return ResponseEntity.ok(response);
	}

}