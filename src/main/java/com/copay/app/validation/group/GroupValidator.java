package com.copay.app.validation.group;

import com.copay.app.entity.Group;
import com.copay.app.exception.group.GroupNotFoundException;

import java.util.Optional;

public class GroupValidator {

    public static Group validateGroupExists(Optional<Group> optional) {
        return optional.orElseThrow(() ->
                new GroupNotFoundException("Group not found with the provided identifier.")
        );
    }
}
