package com.copay.app.service.auth;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.auth.request.UserLoginRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepOneDTO;
import com.copay.app.dto.auth.request.UserRegisterStepTwoDTO;

import com.copay.app.dto.auth.response.RegisterStepOneResponseDTO;
import com.copay.app.dto.auth.response.RegisterStepTwoResponseDTO;
import com.copay.app.dto.auth.response.LoginResponseDTO;
import com.copay.app.entity.User;
import com.copay.app.exception.user.EmailAlreadyExistsException;
import com.copay.app.exception.user.IncorrectPasswordException;
import com.copay.app.exception.user.PhoneAlreadyExistsException;
import com.copay.app.exception.user.UserNotFoundException;
import com.copay.app.repository.RevokedTokenRepository;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
import com.copay.app.service.user.UserAvailabilityServiceImpl;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// Constructor
	public AuthServiceImpl(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
						   JwtService jwtService, UserRepository userRepository, RevokedTokenRepository revokedTokenRepository,
						   UserAvailabilityServiceImpl userValidationService) {

		// Constructor to initialize dependencies.
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public LoginResponseDTO loginUser(UserLoginRequestDTO request) {

		// Find the user by phone number before executing try-catch block.
		User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		try {

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
					user.getUsername(),
					user.getEmail(),
					"true"
			);

		} catch (BadCredentialsException e) {

			throw new IncorrectPasswordException("Invalid password");
		}
	}

	@Transactional
	public RegisterStepOneResponseDTO registerStepOne(UserRegisterStepOneDTO request) {

		boolean emailExists = userRepository.existsByEmail(request.getEmail());

		// Verify if the email exists or not.
		if (emailExists) {
			throw new EmailAlreadyExistsException("Email <" + request.getEmail() + "> already exists.");
		}

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

		return new RegisterStepOneResponseDTO(
				jwtToken,
				expiresIn,
				user.getUsername(),
				user.getEmail());
	}

	@Transactional
    public RegisterStepTwoResponseDTO registerStepTwo(UserRegisterStepTwoDTO request, String token) {

        try {

            JwtService.setCurrentContext(JwtService.TokenValidationContext.REGISTER_STEP_TWO);

            jwtService.validateToken(token);

            boolean phoneNumberExists = userRepository.existsByPhoneNumber(request.getPhoneNumber());

            // Verify if the phoneNumber exists or not.
            if (phoneNumberExists) {
                throw new PhoneAlreadyExistsException("Phone number <" + request.getPhoneNumber() + "> already exists.");
            }

            // Get the email from the current token.
            String emailTemporaryToken = jwtService.getUserIdentifierFromToken(token);

            // Find user by email through the temporal token.
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

            // Return the token trough the DTO.
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

    public void logout(String token) {
    	
        try {

            JwtService.setCurrentContext(JwtService.TokenValidationContext.LOGOUT);

            // Revoke the token when the user logs out
            jwtService.revokeToken(token);

        } finally {

            JwtService.clearContext();
        }
    }
}
