package com.copay.app.service.query;

import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;
import com.copay.app.validation.user.UserValidator;
import org.springframework.stereotype.Service;

/**
 * Service class for user queries (by ID, phone, email).
 * Uses 'existsBy...' methods for efficient existence checks before making full queries.
 * Validation is handled by UserValidator, which uses the repository to fetch and validate data.
 */
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    public UserQueryService(UserRepository userRepository, UserValidator userValidator) {

        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    // Retrieves user by ID, validates through userValidator and returns the user object or custom exception.
    public User getUserById(Long id) {

        return userValidator.validateUserById(userRepository.findById(id), id);
    }

    // Retrieves user by phone number, validates through userValidator and returns the user object or custom exception.
    public User getUserByPhone(String phoneNumber) {

        return userValidator.validateUserByPhone(userRepository.findByPhoneNumber(phoneNumber), phoneNumber);
    }

    // Retrieves user by email, validates through userValidator and returns the user object or custom exception.
    public User getUserByEmail(String email) {

        return userValidator.validateUserByEmail(userRepository.findByEmail(email), email);
    }

    // Checks user existence via repository and delegates exception handling to UserValidator.
    public void existsUserByPhone(String phoneNumber)
    {
        // Verifies existence with the repository.
        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);

        // Delegates exception handling to UserValidator.
        userValidator.validatePhoneExistence(exists, phoneNumber);
    }

    // Checks user existence via repository and delegates exception handling to UserValidator.
    public void existsUserByEmail(String email) {

        // Verifies existence with the repository.
        boolean exists = userRepository.existsByEmail(email);

        // Delegates exception handling to UserValidator.
        userValidator.validateEmailExistence(exists, email);
    }
}