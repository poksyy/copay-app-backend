package com.copay.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.user.UserCreateDTO;
import com.copay.app.dto.user.UserDeleteDTO;
import com.copay.app.dto.user.UserResponseDTO;
import com.copay.app.dto.user.UserUpdateDTO;
import com.copay.app.entity.User;
import com.copay.app.exception.UserNotFoundException;
import com.copay.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserUniquenessValidator userValidationService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			UserUniquenessValidator userValidationService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userValidationService = userValidationService;
	}

	// Retrieves a user and returns it as a DTO (Used in the Controller).
	public UserResponseDTO getUserByIdDTO(Long id) {

		User user = getUserById(id);

		return new UserResponseDTO(user);
	}

	// Internal method that returns the User entity (Used within the Service).
	private User getUserById(Long id) {

		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with provided id"));
	}

	// Retrieves a user and returns it as a DTO (Used in the Controller)
	public UserResponseDTO getUserByPhoneDTO(String phoneNumber) {

		User user = getUserByPhone(phoneNumber);

		return new UserResponseDTO(user);
	}

	// Internal method that returns the User entity (Used within the Service).
	private User getUserByPhone(String phoneNumber) {

		return userRepository.findByPhoneNumber(phoneNumber)
				.orElseThrow(() -> new UserNotFoundException("User not found with provided phone number"));
	}

	// Internal method that returns the User entity (Used within the Service).
	public User getUserByEmail(String email) {

		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with provided mail"));
	}

	public UserResponseDTO createUser(UserCreateDTO request) {

		User tempUser = new User();
		tempUser.setEmail(request.getEmail());
		tempUser.setPhoneNumber(request.getPhoneNumber());

		// Verify if the phone number and/or email exists or not.
		userValidationService.validateUserUniqueness(tempUser);

		// Create user entity and set properties.
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setPhoneNumber(request.getPhoneNumber());
		user.setCreatedAt(LocalDateTime.now());
		user.setCompleted(true);

		// Persists the user on the database.		
		User newUser = userRepository.save(user);

		return new UserResponseDTO(newUser);
	}

	// Update user trough User ID.
	public UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {

		User existingUser = getUserById(id);

		return updateUserDetails(existingUser, userUpdateDTO);
	}

	// Update user trough User email.
	public UserResponseDTO updateUser(String email, UserUpdateDTO userUpdateDTO) {

		User existingUser = getUserByEmail(email);

		return updateUserDetails(existingUser, userUpdateDTO);
	}

	private UserResponseDTO updateUserDetails(User existingUser, UserUpdateDTO userUpdateDTO) {

		// Validate uniqueness.
		userValidationService.validateUserUniqueness(existingUser);

		existingUser.setUsername(userUpdateDTO.getUsername());
		existingUser.setEmail(userUpdateDTO.getEmail());
		existingUser.setPassword(userUpdateDTO.getPassword());
		existingUser.setPhoneNumber(userUpdateDTO.getPhoneNumber());

		// Save updated user.
		User updatedUser = userRepository.save(existingUser);

		return new UserResponseDTO(updatedUser);
	}

	// Deletes a user and returns a response DTO (Used in the Controller)
	public UserDeleteDTO deleteUser(Long id) {

		User user = getUserById(id); // Uses the internal method to find the user

		userRepository.delete(user);

		return new UserDeleteDTO("Your account has been deleted successfully");
	}

	public List<UserResponseDTO> getAllUsers() {

		return userRepository.findAll().stream().map(UserResponseDTO::new).collect(Collectors.toList());
	}
}
