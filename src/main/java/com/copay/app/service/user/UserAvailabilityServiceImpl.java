package com.copay.app.service.user;

import com.copay.app.entity.User;
import com.copay.app.exception.EmailAlreadyExistsException;
import com.copay.app.exception.PhoneAlreadyExistsException;
import com.copay.app.exception.UserUniquenessException;
import com.copay.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAvailabilityServiceImpl implements UserAvailabilityService {

	private final UserRepository userRepository;

	public UserAvailabilityServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void checkUserExistence(User user) {

		boolean phoneInUse = userRepository.existsByPhoneNumber(user.getPhoneNumber());
		boolean emailInUse = userRepository.existsByEmail(user.getEmail());

		if (phoneInUse && emailInUse) {
			throw new UserUniquenessException(String.format(
					"Both phone number '%s' and email '%s' are already in use.",
					user.getPhoneNumber(), user.getEmail()
			));
		}

		if (phoneInUse) {
			throw new PhoneAlreadyExistsException(String.format(
					"Phone number '%s' is already in use.", user.getPhoneNumber()
			));
		}

		if (emailInUse) {
			throw new EmailAlreadyExistsException(String.format(
					"Email '%s' is already in use.", user.getEmail()
			));
		}
	}
}
