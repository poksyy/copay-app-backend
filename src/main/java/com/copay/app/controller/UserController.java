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

import com.copay.app.dto.responses.profile.EmailResponseDTO;
import com.copay.app.dto.responses.profile.PhoneNumberResponseDTO;
import com.copay.app.dto.responses.profile.UsernameResponseDTO;
import com.copay.app.dto.user.UserCreateDTO;
import com.copay.app.dto.user.UserDeleteDTO;
import com.copay.app.dto.user.UserResponseDTO;
import com.copay.app.dto.user.UserUpdateDTO;
import com.copay.app.dto.user.profile.UpdateEmailDTO;
import com.copay.app.dto.user.profile.UpdatePhoneNumberDTO;
import com.copay.app.dto.user.profile.UpdateUsernameDTO;
import com.copay.app.service.ValidationService;
import com.copay.app.service.user.UserService;
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
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO request, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Convert the saved entity to a response DTO.
		UserResponseDTO response = userService.createUser(request);

		return ResponseEntity.ok().body(response);
	}

	// Retrieves a user by their ID and returns it as a DTO.
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

		UserResponseDTO response = userService.getUserByIdDTO(id);

		return ResponseEntity.ok(response);
	}

	// Retrieves a user by their phone number and returns it as a DTO.
	@GetMapping("/phone/{phoneNumber}")
	public ResponseEntity<UserResponseDTO> getUserByPhone(@PathVariable String phoneNumber) {

		UserResponseDTO response = userService.getUserByPhoneDTO(phoneNumber);

		return ResponseEntity.ok(response);
	}

	// Updates a user with the provided ID.
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO request,
			BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Delegate the update to the service.
		UserResponseDTO response = userService.updateUser(id, request);

		return ResponseEntity.ok(response);
	}

	// Updates a user with the provided email.
	@PutMapping("/email/{email}")
	public ResponseEntity<?> updateUserByEmail(@PathVariable String email,
			@Valid @RequestBody UserUpdateDTO request, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Convert the updated entity to a response DTO.
		UserResponseDTO response = userService.updateUser(email, request);

		return ResponseEntity.ok(response);
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
	
	// Updates only the username of a user by ID.
	@PutMapping("/edit-username/{id}")
	public ResponseEntity<?> updateUsername(@PathVariable Long id, @Valid @RequestBody UpdateUsernameDTO request, BindingResult result) {

	    ValidationErrorResponse validationResponse = ValidationService.validate(result);

	    // Validates the DTO annotations.
	    if (validationResponse != null) {
	        return ResponseEntity.badRequest().body(validationResponse);
	    }

	    // Delegate the username update to the service.
	    UsernameResponseDTO response = userService.updateUsername(id, request);

	    return ResponseEntity.ok(response);
	}
	
	// Updates only the phone number of a user by ID.
	@PutMapping("/edit-phone/{id}")
	public ResponseEntity<?> updatePhoneNumber(@PathVariable Long id, @Valid @RequestBody UpdatePhoneNumberDTO request, BindingResult result) {

	    ValidationErrorResponse validationResponse = ValidationService.validate(result);

	    // Validates the DTO annotations.
	    if (validationResponse != null) {
	        return ResponseEntity.badRequest().body(validationResponse);
	    }

	    // Delegate the phone number update to the service.
	    PhoneNumberResponseDTO response = userService.updatePhoneNumber(id, request);

	    return ResponseEntity.ok(response);
	}

	// Updates only the email of a user by ID.
	@PutMapping("/edit-email/{id}")
	public ResponseEntity<?> updateEmail(@PathVariable Long id, @Valid @RequestBody UpdateEmailDTO request, BindingResult result) {

	    ValidationErrorResponse validationResponse = ValidationService.validate(result);

	    // Validates the DTO annotations.
	    if (validationResponse != null) {
	        return ResponseEntity.badRequest().body(validationResponse);
	    }

	    // Delegate the email update to the service.
	    EmailResponseDTO response = userService.updateEmail(id, request);

	    return ResponseEntity.ok(response);
	}

}