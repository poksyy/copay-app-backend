package com.copay.app.validation.user;

import com.copay.app.entity.User;
import com.copay.app.exception.user.EmailAlreadyExistsException;
import com.copay.app.exception.user.PhoneAlreadyExistsException;
import com.copay.app.exception.user.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidator {

    public User validateUserById(Optional<User> optional, Long id ) {

        return optional.orElseThrow(() ->
                new UserNotFoundException("User with ID " + id + " not found.")
        );
    }

    public User validateUserByEmail(Optional<User> optional, String email ) {

        return optional.orElseThrow(() ->
                new UserNotFoundException("User with email " + email + " not found.")
        );
    }

    public User validateUserByPhone(Optional<User> optional, String phoneNumber ) {

        return optional.orElseThrow(() ->
                new UserNotFoundException("User with phone " + phoneNumber + " not found.")
        );
    }

    // Checks if user exists by email and throws an exception if not found.
    public void validateEmailExistence(boolean exists, String email) {

        if (exists) {

            throw new EmailAlreadyExistsException("Email " + email + " already exists");
        }
    }

    // Checks if user exists by ID and throws an exception if not found.
    public void validatePhoneExistence(boolean exists, String phoneNumber) {

        if (exists) {

            throw new PhoneAlreadyExistsException("Phone number " + phoneNumber + " already exists");
        }
    }
}
