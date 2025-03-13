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
	public UserDetails loadUserByUsername(String phoneNumber) {
		// Finds the user by phone number.
		return userRepository.findByPhoneNumber(phoneNumber).map(user ->
		// Creates a UserDetails object with phone number and password.
		User.withUsername(user.getPhoneNumber()).password(user.getPassword()).build()).orElseThrow(() ->
		// Throws custom exception if user is not found.
		new UsernameNotFoundException("Phone not found"));
	}
}
