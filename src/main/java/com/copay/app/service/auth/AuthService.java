package com.copay.app.service.auth;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.JwtResponse;
import com.copay.app.dto.UserLoginRequest;
import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
import com.copay.app.service.UserUniquenessValidator;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserUniquenessValidator userValidationService;

	@Autowired
	public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
			JwtService jwtService, UserRepository userRepository, UserUniquenessValidator userValidationService) {

		// Constructor to initialize dependencies.
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userValidationService = userValidationService;
	}

	public JwtResponse loginUser(UserLoginRequest loginRequest) {

		// Create authentication token with phone number and password.
		User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid phone number or password");
		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getPhoneNumber(), loginRequest.getPassword());

		try {
			// Authenticate user using AuthenticationManager.
			Authentication authentication = authenticationManager.authenticate(authenticationToken);

			// Generate JWT token if authentication is successful.
			String jwtToken = jwtService.generateToken(authentication.getName());
			long expiresIn = jwtService.getExpirationTime();

			return new JwtResponse(jwtToken, expiresIn);

		} catch (BadCredentialsException e) {

			throw new RuntimeException("Invalid phone number or password");

		} catch (UsernameNotFoundException e) {

			throw new RuntimeException("User not found");
		}
	}

	public JwtResponse registerUser(UserRegisterRequest request) {

		// Create user entity.
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		// Encrypt password through the SecurityConfig @Bean.
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(LocalDateTime.now());

		// Verify if the phone number and/or email exists or not.
		userValidationService.validateUserUniqueness(user);

		// Save user to DB.
		userRepository.save(user);

		// Generate JWT token for the newly registered user.
		String jwtToken = jwtService.generateToken(request.getUsername());
		// Get the expiration time of the JWT token.
		long expiresIn = jwtService.getExpirationTime();

		return new JwtResponse(jwtToken, expiresIn);

	}
}
