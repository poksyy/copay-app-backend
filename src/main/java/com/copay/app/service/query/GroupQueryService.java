package com.copay.app.service.query;

import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.repository.GroupRepository;
import com.copay.app.service.JwtService;
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

    private final GroupValidator groupValidator;

    private final UserQueryService userQueryService;

    private final JwtService jwtService;

    public GroupQueryService(GroupRepository groupRepository, GroupValidator groupValidator,
                             UserQueryService userQueryService, JwtService jwtService) {

        this.groupRepository = groupRepository;
        this.groupValidator = groupValidator;
        this.userQueryService = userQueryService;
        this.jwtService = jwtService;
    }

    // Retrieves user by ID, validates through GroupValidator and returns the user object or custom exception.
    public Group getGroupById(Long id) {

        return groupValidator.validateGroupById(groupRepository.findById(id), id);
    }

    public void validateGroupCreator(Group group, String token) {

        // Extract the phone number thanks to the token identifier.
        String phoneNumber = jwtService.getUserIdentifierFromToken(token);

        User user = userQueryService.getUserByPhone(phoneNumber);

        // Validates if the user is the group creator.
        groupValidator.validateGroupCreator(group, user.getUserId());
    }

}