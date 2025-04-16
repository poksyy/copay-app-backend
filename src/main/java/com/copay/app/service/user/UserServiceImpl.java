package com.copay.app.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.copay.app.entity.User;
import com.copay.app.exception.EmailAlreadyExistsException;
import com.copay.app.exception.PhoneAlreadyExistsException;
import com.copay.app.exception.UserNotFoundException;
import com.copay.app.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserAvailabilityServiceImpl userAvailabilityServiceImpl;

	// Constructor
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
						   UserAvailabilityServiceImpl userAvailabilityServiceImpl) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userAvailabilityServiceImpl = userAvailabilityServiceImpl;
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

	@Transactional
	public UserResponseDTO createUser(UserCreateDTO request) {
		User newUser = new User();
		newUser.setEmail(request.getEmail());
		newUser.setPhoneNumber(request.getPhoneNumber());

		// Validate user credentials availability.
		userAvailabilityServiceImpl.checkUserExistence(newUser);

		newUser.setUsername(request.getUsername());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		newUser.setCreatedAt(LocalDateTime.now());
		newUser.setCompleted(true);

		return new UserResponseDTO(userRepository.save(newUser));
	}

	@Transactional
	public UserResponseDTO updateUser(Long id, UserUpdateDTO request) {
		User user = getUserById(id);
		return updateUserDetails(user, request);
	}

	@Transactional
	public UserResponseDTO updateUser(String email, UserUpdateDTO request) {
		User user = getUserByEmail(email);
		return updateUserDetails(user, request);
	}

	private UserResponseDTO updateUserDetails(User user, UserUpdateDTO request) {
		// Update logic here
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		// Check user availability
		userAvailabilityServiceImpl.checkUserExistence(user);

		return new UserResponseDTO(userRepository.save(user));
	}

	@Transactional
	public UserDeleteDTO deleteUser(Long userId) {
		User user = getUserById(userId);
		userRepository.delete(user);
		return new UserDeleteDTO("User account deleted successfully.");
	}

	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAll().stream().map(UserResponseDTO::new).collect(Collectors.toList());
	}

	@Transactional
	public UsernameResponseDTO updateUsername(Long id, UpdateUsernameDTO request) {
		User user = getUserById(id);

		user.setUsername(request.getUsername());

		return new UsernameResponseDTO(userRepository.save(user));
	}

	@Transactional
	public PhoneNumberResponseDTO updatePhoneNumber(Long id, UpdatePhoneNumberDTO request) {
		User user = getUserById(id);

		// Validates if the new phone number is already taken.
		boolean phoneNumberExists = userRepository.existsByPhoneNumber(request.getPhoneNumber());

		// Verify if the phoneNumber exists or not.
		if (phoneNumberExists) {
			throw new PhoneAlreadyExistsException("Phone number <" + request.getPhoneNumber() + "> already exists.");
		}

		user.setPhoneNumber(request.getPhoneNumber());

		return new PhoneNumberResponseDTO(userRepository.save(user));
	}

	@Transactional
	public EmailResponseDTO updateEmail(Long id, UpdateEmailDTO request) {
		User user = getUserById(id);

		// Validates if the new email is already taken.
		boolean emailExists = userRepository.existsByEmail(request.getEmail());

		// Verify if the email exists or not.
		if (emailExists) {
			throw new EmailAlreadyExistsException("Email <" + request.getEmail() + "> already exists.");
		}

		user.setEmail(request.getEmail());

		return new EmailResponseDTO(userRepository.save(user));
	}

}
