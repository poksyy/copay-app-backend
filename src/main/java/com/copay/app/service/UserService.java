package com.copay.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.entity.User;
import com.copay.app.exception.UserNotFoundException;
import com.copay.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserUniquenessValidator userValidationService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserUniquenessValidator userValidationService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userValidationService = userValidationService;
	}

	public User createUser(User user) {

		// Verify if the phone number and/or email exists or not.
		userValidationService.validateUserUniqueness(user);

		// Encode the password.
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Persists the user on the database.
		return userRepository.save(user);
	}

	public User getUserById(Long id) {
		
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}

	public User getUserByPhone(String phoneNumber) {

		return userRepository.findByPhoneNumber(phoneNumber)
				.orElseThrow(() -> new RuntimeException("Phone number not found"));
	}

	public User updateUser(Long id, User userData) {

		User user = getUserById(id);
		user.setUsername(userData.getUsername());
		user.setEmail(userData.getEmail());
		user.setPhoneNumber(userData.getPhoneNumber());

		// Verify if the phone number and/or email exists or not.
		userValidationService.validateUserUniqueness(user);

		return userRepository.save(user);
	}

	public String deleteUser(Long id) {
		
		User user = userRepository.findById(id)
		    .orElseThrow(() -> new UserNotFoundException("User with id <" + id + "> not found."));
		
		userRepository.delete(user);
		
		return "User has been deleted successfully.";
	}


	public List<User> getAllUsers() {

		return userRepository.findAll();
	}
	
	@Transactional
	public void updatePhoneNumber(String email, String phoneNumber) {
		
		// Find user by email.
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		// Phone number can not be updated to empty or null.
		if (phoneNumber == null || phoneNumber.isBlank()) {
			throw new IllegalArgumentException("Phone number cannot be empty");
		}

		// Check if the phone number is already in use by another user.
		Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
		if (existingUser.isPresent() && !existingUser.get().getEmail().equals(email)) {
			throw new IllegalArgumentException("Phone number is already registered");
		}

		// Update the user's phone number and mark user the registration as completed.
		user.setPhoneNumber(phoneNumber);
		user.setCompleted(true);
		userRepository.save(user);
	}

}
