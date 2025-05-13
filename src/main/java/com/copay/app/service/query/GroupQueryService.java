package com.copay.app.service.query;

import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.repository.GroupRepository;
import com.copay.app.validation.group.GroupValidator;
import org.springframework.stereotype.Service;

/**
 * Service class for user queries (by ID, phone, email).
 * Uses 'existsBy...' methods for efficient existence checks before making full queries.
 * Validation is handled by GroupValidator, which uses the repository to fetch and validate data.
 */
@Service
public class GroupQueryService {

    private final GroupRepository groupRepository;

    private final GroupValidator GroupValidator;

    public GroupQueryService(GroupRepository groupRepository, GroupValidator GroupValidator) {

        this.groupRepository = groupRepository;
        this.GroupValidator = GroupValidator;
    }

    // Retrieves user by ID, validates through GroupValidator and returns the user object or custom exception.
    public Group getGroupById(Long id) {

        return GroupValidator.validateGroupById(groupRepository.findById(id), id);
    }
}