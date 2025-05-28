package com.copay.app.service.query;

import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.exception.user.UserNotFoundException;
import com.copay.app.repository.GroupMemberRepository;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
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

    private final GroupMemberRepository groupMemberRepository;
    private final JwtService jwtService;

    public UserQueryService(UserRepository userRepository, UserValidator userValidator, GroupMemberRepository groupMemberRepository, JwtService jwtService) {

        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.groupMemberRepository = groupMemberRepository;
        this.jwtService = jwtService;
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

    // Checks user availability in the group via repository and delegates exception handling to UserValidator.
    public void validateUserInGroup(Long userId, Long groupId) {

        // Verifies existence with the repository.
        boolean exists = groupMemberRepository.existsByIdUserUserIdAndIdGroupGroupId(userId, groupId);

        // Delegates exception handling to UserValidator.
        userValidator.validateUserInGroup(exists, userId, groupId);
    }

    // Compares if the userId is equal or not with the tokenId and delegates exception handling to UserValidator.
    public void validateUserIdMatchesToken(Long userId, String token) {

        // Extracts the phone number from the token.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        // Finds the user thanks to the phoneNumber and validates if it exists.
        User userFromToken = getUserByPhone(phoneNumber);

        // Delegates exception handling to UserValidator.
        userValidator.validateUserIdMatchesToken(userId, userFromToken.getUserId());
    }
}