package com.copay.app.service.user;

import java.time.LocalDateTime;
import java.util.List;
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

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserAvailabilityService userAvailabilityService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
					   UserAvailabilityService userAvailabilityService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userAvailabilityService = userAvailabilityService;
	}

	public UserResponseDTO getUserByIdDTO(Long id) {
		return new UserResponseDTO(getUserById(id));
	}

	private User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with provided ID."));
	}

	public UserResponseDTO getUserByPhoneDTO(String phoneNumber) {
		return new UserResponseDTO(getUserByPhone(phoneNumber));
	}

	private User getUserByPhone(String phoneNumber) {
		return userRepository.findByPhoneNumber(phoneNumber)
				.orElseThrow(() -> new UserNotFoundException("User not found with provided phone number."));
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with provided email."));
	}

	public UserResponseDTO createUser(UserCreateDTO request) {
		User newUser = new User();
		newUser.setEmail(request.getEmail());
		newUser.setPhoneNumber(request.getPhoneNumber());

		// Validate uniqueness
		userAvailabilityService.checkUserExistence(newUser);

		newUser.setUsername(request.getUsername());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		newUser.setCreatedAt(LocalDateTime.now());
		newUser.setCompleted(true);

		return new UserResponseDTO(userRepository.save(newUser));
	}

	public UserResponseDTO updateUser(Long id, UserUpdateDTO request) {
		return updateUserDetails(getUserById(id), request);
	}

	public UserResponseDTO updateUser(String email, UserUpdateDTO request) {
		return updateUserDetails(getUserByEmail(email), request);
	}

	private UserResponseDTO updateUserDetails(User existingUser, UserUpdateDTO request) {
		User tempUser = new User();
		tempUser.setEmail(request.getEmail());
		tempUser.setPhoneNumber(request.getPhoneNumber());

		// Validate uniqueness
		userAvailabilityService.checkUserExistence(tempUser);

		existingUser.setUsername(request.getUsername());
		existingUser.setEmail(request.getEmail());
		existingUser.setPassword(passwordEncoder.encode(request.getPassword())); // Fixed encoding
		existingUser.setPhoneNumber(request.getPhoneNumber());

		return new UserResponseDTO(userRepository.save(existingUser));
	}

	public UserDeleteDTO deleteUser(Long id) {
		User user = getUserById(id);
		userRepository.delete(user);
		return new UserDeleteDTO("User account deleted successfully.");
	}

	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAll().stream()
				.map(UserResponseDTO::new)
				.collect(Collectors.toList());
	}
}
