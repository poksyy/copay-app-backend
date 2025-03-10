package com.copay.app.service;

import com.copay.app.dto.JwtResponse;
import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public JwtResponse registerUser(UserRegisterRequest request) {

		// Check if username or email is already taken.
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		// Create user entity.
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		// Encrypt password.
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(LocalDateTime.now());

		// Save user to DB.
		userRepository.save(user);

		// Generate JWT token for the newly registered user.
		String jwtToken = jwtService.generateToken(request.getUsername());
		// Get the expiration time of the JWT token.
		long expiresIn = jwtService.getExpirationTime();

		return new JwtResponse(jwtToken, expiresIn);

	}
}
