package com.copay.app.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.copay.app.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String identifier) {

		// Token is null.
		if (identifier == null || identifier.isBlank()) {
			throw new UsernameNotFoundException("Identifier cannot be null or empty");
		}

		// Token contains an email.
		else if (identifier.contains("@")) {
			return loadUserByEmail(identifier);

		// Token contains a phone number.
		} else {
			return loadUserByPhoneNumber(identifier);
		}
	}

	// Method used in --> RegisterStepTwo and LoginUser endpoints
	public UserDetails loadUserByPhoneNumber(String phoneNumber) {
		// Finds the user by phone number.
		return userRepository.findByPhoneNumber(phoneNumber).map(user ->
		// Creates a UserDetails object with phone number and password.
		User.withUsername(user.getPhoneNumber()).password(user.getPassword()).build()).orElseThrow(() ->
		// Throws custom exception if user is not found.
		new UsernameNotFoundException("Phone not found"));
	}

	// Method used in --> RegisterStepOne endpoint.
	public UserDetails loadUserByEmail(String email) {
		// Finds the user by email.
		return userRepository.findByEmail(email).map(user ->
		// Creates a UserDetails object with email and password.
		User.withUsername(user.getEmail()).password(user.getPassword()).build()).orElseThrow(() ->
		// Throws custom exception if email is not found.
		new UsernameNotFoundException("Email not found"));
	}

}
