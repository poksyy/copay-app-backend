package com.copay.app.validation.group;

import com.copay.app.entity.Group;
import com.copay.app.exception.group.GroupNotFoundException;
import com.copay.app.exception.group.InvalidGroupCreatorException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GroupValidator {

    public Group validateGroupById(Optional<Group> optional, Long id ) {

        return optional.orElseThrow(() ->
                new GroupNotFoundException("Group with ID " + id + " not found.")
        );
    }

    public void validateGroupCreator(Group group, Long userId) {

        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new InvalidGroupCreatorException(
                    "User " + userId + " has no permissions on the group " + group.getGroupId());
        }
    }
}