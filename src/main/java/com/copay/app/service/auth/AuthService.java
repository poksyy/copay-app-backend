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
import com.copay.app.dto.auth.UserLoginRequest;
import com.copay.app.dto.auth.UserRegisterStepOneDTO;
import com.copay.app.dto.auth.UserRegisterStepTwoDTO;
import com.copay.app.entity.User;
import com.copay.app.exception.EmailAlreadyExistsException;
import com.copay.app.exception.UserNotFoundException;
import com.copay.app.repository.RevokedTokenRepository;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
import com.copay.app.service.UserUniquenessValidator;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
			JwtService jwtService, UserRepository userRepository, RevokedTokenRepository revokedTokenRepository,
			UserUniquenessValidator userValidationService) {

		// Constructor to initialize dependencies.
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public JwtResponse loginUser(UserLoginRequest loginRequest) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getPhoneNumber(), loginRequest.getPassword());

		try {
			
			// Authenticate user using AuthenticationManager.
			Authentication authentication = authenticationManager.authenticate(authenticationToken);

			// Generate JWT token if authentication is successful.
			String jwtToken = jwtService.generateToken(authentication.getName());
			long expiresIn = jwtService.getExpirationTime(true);

			return new JwtResponse(jwtToken, expiresIn);

		} catch (BadCredentialsException e) {

			throw new RuntimeException("Invalid phone number or password");

		} catch (UsernameNotFoundException e) {

			throw new RuntimeException("User not found");
		}
	}

	public JwtResponse registerStepOne(UserRegisterStepOneDTO request) {

		boolean emailExists = userRepository.existsByEmail(request.getEmail());
		
		// Verify if the email exists or not.
		if (emailExists) {
			throw new EmailAlreadyExistsException("Email <" + request.getEmail() + "> already exists.");
		}
		
		// TODO: CREATE PASSWORD CUSTOM VALIDATIONS

		// Create user entity.
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		// Encrypt password through the SecurityConfig @Bean.
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(LocalDateTime.now());
		user.setPhoneNumber(null);
		user.setCompleted(false);

		// Save user to DB.
		userRepository.save(user);

		// Generate JWT token for the newly registered user.
		String jwtToken = jwtService.generateTemporaryToken(request.getEmail());
		// Get the expiration time of the JWT token.
		long expiresIn = jwtService.getExpirationTime(false);

		return new JwtResponse(jwtToken, expiresIn);
	}

	public JwtResponse registerStepTwo(UserRegisterStepTwoDTO request, String token) {

		boolean phoneNumberExists = userRepository.existsByPhoneNumber(request.getPhoneNumber());

		// Verify if the phoneNumber exists or not.
		if (phoneNumberExists) {
			throw new EmailAlreadyExistsException("Phone number <" + request.getPhoneNumber() + "> already exists.");
		}

		// Get the email from the current token.
		String emailTemporaryToken = jwtService.getUserIdentifierFromToken(token);

		// Find user by email trough the temporal token.
		User user = userRepository.findByEmail(emailTemporaryToken)
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		// Update the user's phone number and mark user registration as completed.
		user.setPhoneNumber(request.getPhoneNumber());
		user.setCompleted(true);
		
		userRepository.save(user);

		// Revoke the step-one token.
		jwtService.revokeToken(token);
		
		// Create the 1 hour with the phone number.
		String jwtToken = jwtService.generateToken(request.getPhoneNumber());
		long expiresIn = jwtService.getExpirationTime(true);

		// Return the token brought the DTO.
		return new JwtResponse(jwtToken, expiresIn);
	}

	public void logout(String token) {
		
        // Revoke the token when the user logs out
        jwtService.revokeToken(token);
	}
}
