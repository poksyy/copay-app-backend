package com.copay.app.service;

import com.copay.app.entity.User;
import com.copay.app.exception.EmailAlreadyExistsException;
import com.copay.app.exception.PhoneAlreadyExistsException;
import com.copay.app.exception.UserUniquenessException;
import com.copay.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserUniquenessValidator {

	private final UserRepository userRepository;

	public UserUniquenessValidator(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void validateUserUniqueness(User user) {
		
		boolean phoneExists = userRepository.existsByPhoneNumber(user.getPhoneNumber());
		boolean emailExists = userRepository.existsByEmail(user.getEmail());

		if (phoneExists && emailExists) {
			throw new UserUniquenessException(
					"Phone number <" + user.getPhoneNumber() + "> and Email <" + user.getEmail() + "> already exist.");
		}

		if (phoneExists) {
			throw new PhoneAlreadyExistsException("Phone number <" + user.getPhoneNumber() + "> already exists.");
		}

		if (emailExists) {
			throw new EmailAlreadyExistsException("Email <" + user.getEmail() + "> already exists.");
		}
	}
}
