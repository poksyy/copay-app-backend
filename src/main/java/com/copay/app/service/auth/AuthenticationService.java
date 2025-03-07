package com.copay.app.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.copay.app.dto.UserLoginRequest;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
import com.copay.app.dto.JwtResponse;

@Service
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Autowired
	public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService,
			UserRepository userRepository) {
		
		// Constructor to initialize dependencies.
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public JwtResponse authenticateUser(UserLoginRequest loginRequest) {

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
}
