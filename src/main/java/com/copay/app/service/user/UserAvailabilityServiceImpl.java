package com.copay.app.service.user;

import com.copay.app.entity.User;
import com.copay.app.exception.user.EmailAlreadyExistsException;
import com.copay.app.exception.user.PhoneAlreadyExistsException;
import com.copay.app.exception.user.UserUniquenessException;
import com.copay.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAvailabilityServiceImpl implements UserAvailabilityService {

	private final UserRepository userRepository;

	public UserAvailabilityServiceImpl(UserRepository userRepository) {

		this.userRepository = userRepository;
	}

	@Override
	public void checkUserExistence(User user) {

		Optional<User> existingByEmail = userRepository.findByEmail(user.getEmail());
		Optional<User> existingByPhone = userRepository.findByPhoneNumber(user.getPhoneNumber());

		boolean emailInUse = existingByEmail.isPresent() && !existingByEmail.get().getUserId().equals(user.getUserId());
		boolean phoneInUse = existingByPhone.isPresent() && !existingByPhone.get().getUserId().equals(user.getUserId());

		if (emailInUse && phoneInUse) {
			throw new UserUniquenessException(String.format(
					"Both phone number '%s' and email '%s' are already in use.",
					user.getPhoneNumber(), user.getEmail()
			));
		}

		if (emailInUse) {
			throw new EmailAlreadyExistsException(String.format(
					"Email '%s' is already in use.", user.getEmail()
			));
		}

		if (phoneInUse) {
			throw new PhoneAlreadyExistsException(String.format(
					"Phone number '%s' is already in use.", user.getPhoneNumber()
			));
		}
	}

}
