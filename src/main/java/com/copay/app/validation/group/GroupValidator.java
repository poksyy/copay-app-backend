package com.copay.app.validation.group;

import com.copay.app.entity.Group;
import com.copay.app.exception.group.GroupNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GroupValidator {

    public Group validateGroupById(Optional<Group> optional, Long id ) {

        return optional.orElseThrow(() ->
                new GroupNotFoundException("Group with ID " + id + " not found.")
        );
    }
}