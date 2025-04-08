package com.copay.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.copay.app.dto.group.GroupRequestDTO;
import com.copay.app.dto.responses.GroupResponseDTO;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.GroupMemberId;
import com.copay.app.exception.UserAlreadyMemberException;
import com.copay.app.repository.GroupMemberRepository;
import com.copay.app.repository.GroupRepository;
import com.copay.app.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	// Injection of UserRepository to find users by phoneNumber.
	@Autowired
	private UserRepository userRepository;

	// Use EntityManager to obtain a reference to the User entity
	@PersistenceContext
	private EntityManager entityManager;

    @Transactional
    public GroupResponseDTO createGroup(GroupRequestDTO request) {

        // Get a reference to the creator user
        User creator = entityManager.getReference(User.class, request.getCreatedBy());

        // Create the group entity
        Group group = new Group();
        group.setGroupName(request.getGroupName());
        group.setCreatedBy(creator);
        group.setDescription(request.getDescription());
        group.setEstimatedPrice(request.getEstimatedPrice());
        group.setCurrency(request.getCurrency());
        group.setImageUrl(request.getImageUrl());
        group.setImageProvider(request.getImageProvider());

        // Persist the group in the database
        group = groupRepository.save(group);

        // Add the creator as a member
        GroupMemberId groupMemberId = new GroupMemberId(group, creator);
        GroupMember groupMember = new GroupMember(groupMemberId);
        groupMemberRepository.save(groupMember);

        // Add the invited users as group members based on their phone numbers
        for (String phoneNumber : request.getInvitedMembers()) {
        	
            User invitedUser = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new EntityNotFoundException("User with phone number " + phoneNumber + " not found"));

            GroupMemberId invitedGroupMemberId = new GroupMemberId(group, invitedUser);
            
            if (groupMemberRepository.existsById(invitedGroupMemberId)) {
                throw new UserAlreadyMemberException("User with phone number " + phoneNumber + " is already a member");
            }

            GroupMember invitedGroupMember = new GroupMember(invitedGroupMemberId);
            groupMemberRepository.save(invitedGroupMember);
        }

        return mapToGroupResponseDTO(group);
    }

	@Transactional(readOnly = true)
	public List<GroupResponseDTO> getGroupsByUser(Long userId) {

		// Retrieve groups by querying the group_members table for the given user.
		List<GroupMember> groupMembers = groupMemberRepository.findByIdUserUserId(userId);
		
		return groupMembers.stream().map(gm -> mapToGroupResponseDTO(gm.getId().getGroup()))
				.collect(Collectors.toList());
	}

	@Transactional
	public GroupResponseDTO joinGroup(Long groupId, Long userId) {

		// Retrieve the group
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new EntityNotFoundException("Group not found"));

		// Get a reference to the user
		User user = entityManager.getReference(User.class, userId);

		// Check if the user is already a member of the group
		GroupMemberId groupMemberId = new GroupMemberId(group, user);

		if (groupMemberRepository.existsById(groupMemberId)) {
			throw new UserAlreadyMemberException("User is already a member of the group");
		}

		// Add the user as a member
		GroupMember groupMember = new GroupMember(groupMemberId);
		groupMemberRepository.save(groupMember);

		return mapToGroupResponseDTO(group);
	}

	// Helper method to map Group entity to GroupResponseDTO.
	private GroupResponseDTO mapToGroupResponseDTO(Group group) {
	    
	    GroupResponseDTO groupResponseDTO = new GroupResponseDTO();
	    
	    // Set group details.
	    groupResponseDTO.setGroupId(group.getGroupId());
	    groupResponseDTO.setGroupName(group.getGroupName());
	    groupResponseDTO.setCreatedBy(group.getCreatedBy().getUserId());
	    groupResponseDTO.setCreatedAt(group.getCreatedAt());

	    // Set member IDs if group has members.
	    if (group.getGroupMembers() != null) {
	        groupResponseDTO.setMemberIds(group.getGroupMembers().stream()
	            .map(gm -> gm.getId().getUser().getUserId())
	            .collect(Collectors.toList()));
	    }
	    return groupResponseDTO;
	}
}
