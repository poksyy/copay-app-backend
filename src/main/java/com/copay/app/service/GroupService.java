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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	// Use EntityManager to obtain a reference to the User entity
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public GroupResponseDTO createGroup(GroupRequestDTO request) {

		// Get a reference to the creator user.
		User creator = entityManager.getReference(User.class, request.getCreatedBy());

		// Create and save the group entity
		Group group = new Group();
		group.setGroupName(request.getGroupName());
		group.setCreatedBy(creator);
		group = groupRepository.save(group);

		// Optionally, add the creator as a member automatically
		GroupMemberId groupMemberId = new GroupMemberId(group, creator);
		GroupMember groupMember = new GroupMember(groupMemberId);
		groupMemberRepository.save(groupMember);

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

	// Helper method to map a Group entity to its DTO
	private GroupResponseDTO mapToGroupResponseDTO(Group group) {

		GroupResponseDTO groupResponseDTO = new GroupResponseDTO();

		groupResponseDTO.setGroupId(group.getGroupId());
		groupResponseDTO.setGroupName(group.getGroupName());
		groupResponseDTO.setCreatedBy(group.getCreatedBy().getUserId());
		groupResponseDTO.setCreatedAt(group.getCreatedAt());

		if (group.getGroupMembers() != null) {

			groupResponseDTO.setMemberIds(group.getGroupMembers().stream().map(gm -> gm.getId().getUser().getUserId())
					.collect(Collectors.toList()));
		}
		return groupResponseDTO;
	}
}
