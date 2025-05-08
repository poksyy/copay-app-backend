package com.copay.app.utils;

import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.exception.group.GroupNotFoundException;
import com.copay.app.exception.user.UserNotFoundException;

import java.util.Optional;

public class EntityValidator {

    public static User validateUserExists(Optional<User> optional) {
        return optional.orElseThrow(() ->
                new UserNotFoundException("User not found with the provided identifier.")
        );
    }

    public static User validateUserByEmail(Optional<User> optional) {
        return optional.orElseThrow(() ->
                new UserNotFoundException("User not found with the provided email.")
        );
    }

    public static User validateUserByPhone(Optional<User> optional) {
        return optional.orElseThrow(() ->
                new UserNotFoundException("User not found with the provided phone number.")
        );
    }

    // Puedes hacer lo mismo para otras entidades
    public static Group validateGroupExists(Optional<Group> optional) {
        return optional.orElseThrow(() ->
                new GroupNotFoundException("Group not found with the provided identifier.")
        );
    }

}
