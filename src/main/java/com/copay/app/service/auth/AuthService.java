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
import com.copay.app.dto.UserLoginRequest;
import com.copay.app.dto.UserRegisterRequest;
import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
import com.copay.app.dto.JwtResponse;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService,
			UserRepository userRepository) {
		
		// Constructor to initialize dependencies.
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public JwtResponse loginUser(UserLoginRequest loginRequest) {

		// Create authentication token with username and password.
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword());

		try {
			
			// Authenticate user using Authen ticationManager.
			Authentication authentication = authenticationManager.authenticate(authenticationToken);

			// Generate JWT token if authentication is successful.
			String jwtToken = jwtService.generateToken(authentication.getName());
			// Get the expiration time of the JWT token getter.
			long expiresIn = jwtService.getExpirationTime();

			return new JwtResponse(jwtToken, expiresIn);

		} catch (BadCredentialsException e) {
			
			// Throw exception if credentials are invalid.
			throw new RuntimeException("Invalid username or password");

		} catch (UsernameNotFoundException e) {
			
			throw new RuntimeException("User not found");
		}
		
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
			// Encrypt password through the SecurityConfig @Bean.
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
