package com.copay.app.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import com.copay.app.dto.auth.request.GoogleLoginRequestDTO;
import com.copay.app.service.query.UserQueryService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.auth.request.UserLoginRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepOneRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepTwoRequestDTO;

import com.copay.app.dto.auth.response.RegisterStepOneResponseDTO;
import com.copay.app.dto.auth.response.RegisterStepTwoResponseDTO;
import com.copay.app.dto.auth.response.LoginResponseDTO;
import com.copay.app.entity.User;
import com.copay.app.exception.user.IncorrectPasswordException;
import com.copay.app.exception.user.UserNotFoundException;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final UserQueryService userQueryService;

	private final JwtDecoder jwtDecoder;

	// Constructor.
	public AuthServiceImpl(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
						   JwtService jwtService, UserRepository userRepository, UserQueryService userQueryService,
						   JwtDecoder jwtDecoder) {

		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userQueryService = userQueryService;
		this.jwtDecoder = jwtDecoder;
	}

	@Override
	@Transactional
	public LoginResponseDTO loginUser(UserLoginRequestDTO request) {

		// Find a user via UserQueryService, which delegates exception handling to UserValidator.
		User user = userQueryService.getUserByPhone(request.getPhoneNumber());

		// Validates if the password is correct
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IncorrectPasswordException("Invalid password");
		}

		// Encapsulates the user's provided credentials.
		UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(
				request.getPhoneNumber(), request.getPassword());

		// Authenticates encapsulated credentials with the AuthenticationManager.
		Authentication authentication = authenticationManager.authenticate(authenticationRequest);

		// Generates a JWT token with the phone number if authentication is successful.
		String jwtToken = jwtService.generateToken(authentication.getName());

		long expiresIn = jwtService.getExpirationTime(true);

		return new LoginResponseDTO(
				jwtToken,
				expiresIn,
				user.getUserId(),
				user.getPhoneNumber(),
				user.getPhonePrefix(),
				user.getUsername(),
				user.getEmail(),
				"true"
		);
	}

	@Override
	@Transactional
	public RegisterStepOneResponseDTO registerStepOne(UserRegisterStepOneRequestDTO request) {

		// Checks if email exists via UserQueryService, which delegates exception handling to UserValidator.
		userQueryService.existsUserByEmail(request.getEmail());

		// Create a user entity.
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		// Encrypt password through the SecurityConfig @Bean.
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(LocalDateTime.now());
		user.setPhonePrefix(null);
		user.setPhoneNumber(null);
		user.setCompleted(false);

		// Save user to DB.
		userRepository.save(user);

		// Generate JWT token for the newly registered user.
		String jwtToken = jwtService.generateTemporaryToken(request.getEmail());
		// Get the expiration time of the JWT token.
		long expiresIn = jwtService.getExpirationTime(false);

		return new RegisterStepOneResponseDTO(
				jwtToken,
				expiresIn,
				user.getUsername(),
				user.getEmail());
	}

	@Override
	@Transactional
	public RegisterStepTwoResponseDTO registerStepTwo(UserRegisterStepTwoRequestDTO request, String token) {

		try {

			JwtService.setCurrentContext(JwtService.TokenValidationContext.REGISTER_STEP_TWO);

			jwtService.validateToken(token);

			// Checks if a phone number exists via UserQueryService, which delegates exception handling to UserValidator.
			userQueryService.existsUserByPhone(request.getPhoneNumber());

			// Get the email from the current token.
			String emailTemporaryToken = jwtService.getUserIdentifierFromToken(token);

			// Find a user by email through the temporal token.
			User user = userRepository.findByEmail(emailTemporaryToken)
					.orElseThrow(() -> new UserNotFoundException("User not found"));

			// Update the user's phone number and mark user registration as completed.
			user.setPhonePrefix(request.getPhonePrefix());
			user.setPhoneNumber(request.getPhoneNumber());
			user.setCompleted(true);

			userRepository.save(user);

			// Revoke the step-one token.
			jwtService.revokeToken(token);

			// Create the 1 hour with the phone number.
			String jwtToken = jwtService.generateToken(request.getPhoneNumber());
			long expiresIn = jwtService.getExpirationTime(true);

			// Return the token through the DTO.
			return new RegisterStepTwoResponseDTO(
					jwtToken,
					expiresIn,
					user.getUserId(),
					user.getPhoneNumber(),
					user.getUsername(),
					user.getEmail());
		} finally {

			JwtService.clearContext();
		}
	}

	@Override
	@Transactional
	public void logout(String token) {

		try {

			JwtService.setCurrentContext(JwtService.TokenValidationContext.LOGOUT);

			// Revoke the token when the user logs out
			jwtService.revokeToken(token);

		} finally {

			JwtService.clearContext();
		}
	}

	@Override
	@Transactional
	public LoginResponseDTO loginWithGoogle(GoogleLoginRequestDTO request) {

		// Decode and verify the Google ID token.
		Jwt jwt = jwtDecoder.decode(request.getIdToken());

		// Extract user information from the token
		String googleId = jwt.getSubject();
		String email = jwt.getClaimAsString("email");
		String name = jwt.getClaimAsString("name");

		// Try to find user by Google Ads.
		User user = userRepository.findByGoogleId(googleId).orElse(null);

		if (user == null) {
			// Try to find user by email
			user = userRepository.findByEmail(email).orElse(null);

			if (user != null) {
				if (user.getGoogleId() == null) {
					// Associate existing user with Google account
					user.setGoogleId(googleId);
					userRepository.save(user);
				} else {
					// Conflict: email already linked to another Google account
					throw new OAuth2AuthenticationException("This email is already linked to another Google account.");
				}
			}
		}

		if (user == null) {
			// Create a new Google user (force phone input later)
			user = new User();
			user.setGoogleId(googleId);
			user.setEmail(email);
			user.setUsername(name);

			// Force no phone number on first Google login
			user.setPhoneNumber(null);
			user.setPhonePrefix(null);
			user.setCompleted(false);

			// Placeholder password
			String randomPassword = UUID.randomUUID().toString();
			user.setPassword(passwordEncoder.encode(randomPassword));

			user.setCreatedAt(LocalDateTime.now());

			userRepository.save(user);

			// Generate temporary token and redirect to registerStepTwo
			String tempToken = jwtService.generateTemporaryToken(email);
			long expiresIn = jwtService.getExpirationTime(false);

			return new LoginResponseDTO(
					tempToken,
					expiresIn,
					user.getUserId(),
					null,
					null,
					user.getUsername(),
					user.getEmail(),
					"false"
			);
		}

		if (!user.isCompleted()) {
			String tempToken = jwtService.generateTemporaryToken(email);
			long expiresIn = jwtService.getExpirationTime(false);

			return new LoginResponseDTO(
					tempToken,
					expiresIn,
					user.getUserId(),
					user.getPhoneNumber(),
					user.getPhonePrefix(),
					user.getUsername(),
					user.getEmail(),
					"false"
			);
		}

		String jwtToken = jwtService.generateToken(user.getPhoneNumber());
		long expiresIn = jwtService.getExpirationTime(true);

		return new LoginResponseDTO(
				jwtToken,
				expiresIn,
				user.getUserId(),
				user.getPhoneNumber(),
				user.getPhonePrefix(),
				user.getUsername(),
				user.getEmail(),
				"true"
		);
	}
}