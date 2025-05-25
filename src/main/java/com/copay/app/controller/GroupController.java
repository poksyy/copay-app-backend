package com.copay.app.controller;

import java.util.Map;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.group.response.GetGroupMembersResponseDTO;
import com.copay.app.dto.group.request.*;

import com.copay.app.service.group.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.group.response.GroupResponseDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

	private final GroupService groupService;

	// Constructor.
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

	// Endpoint to retrieve a single group by its group ID.
	@GetMapping("/{groupId}/group")
	public ResponseEntity<?> getGroupByGroupId(@PathVariable Long groupId,
											   @Valid @ModelAttribute GetGroupsByGroupRequestDTO getGroupsByGroupRequestDTO) {

		// The userId is manually added to the DTO for validation.
		getGroupsByGroupRequestDTO.setGroupId(groupId);

		GroupResponseDTO GroupResponseDTO = groupService.getGroupByGroupId(groupId);

		return ResponseEntity.ok(GroupResponseDTO);
	}

	// Endpoint to retrieve groups for a given user (HomeScreen display).
	@GetMapping("/{userId}")
	public ResponseEntity<?> getGroupsByUser(@PathVariable Long userId,
			@Valid @ModelAttribute GetGroupsByUserRequestDTO getGroupsByUserRequestDTO) {

		// The userId is manually added to the DTO for validation.
		getGroupsByUserRequestDTO.setUserId(userId);

		GetGroupResponseDTO getGroupResponseDTO = groupService.getGroupsByUserId(userId);

		return ResponseEntity.ok(getGroupResponseDTO);
	}

	@GetMapping("/{groupId}/members")
	public ResponseEntity<?> getGroupMembersByGroup(@PathVariable Long groupId,
													@Valid @ModelAttribute GetGroupsByGroupRequestDTO getGroupsByGroupRequestDTO) {

		// The groupId is manually added to the DTO for validation.
		getGroupsByGroupRequestDTO.setGroupId(groupId);

		GetGroupMembersResponseDTO getGroupMembersResponseDTO = groupService.getGroupMembersByGroup(groupId);

		return ResponseEntity.ok(getGroupMembersResponseDTO);
	}

	// Endpoint to create a new group.
	@PostMapping
	public ResponseEntity<?> createGroup(@RequestBody @Valid CreateGroupRequestDTO createGroupRequestDTO) {

		GroupResponseDTO groupResponseDTO = groupService.createGroup(createGroupRequestDTO);

		return ResponseEntity.ok(groupResponseDTO);
	}

	@PatchMapping("/{groupId}")
	public ResponseEntity<?> updateGroup(@PathVariable Long groupId, @RequestBody Map<String, Object> fieldChanges) {

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO messageResponseDTO = groupService.updateGroup(groupId, fieldChanges, token);

		return ResponseEntity.ok(messageResponseDTO);
	}

	@PatchMapping("/{groupId}/estimatedprice")
	public ResponseEntity<?> updateGroupEstimatedPrice(@PathVariable Long groupId, @RequestBody @Valid UpdateGroupEstimatedPriceRequestDTO request) {

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO messageResponseDTO = groupService.updateGroupEstimatedPrice(groupId, request, token);

		return ResponseEntity.ok(messageResponseDTO);
	}

	@PatchMapping("/{groupId}/registeredmembers")
	public ResponseEntity<?> updateGroupRegisteredMembers(@PathVariable Long groupId,
			@RequestBody @Valid UpdateGroupRegisteredMembersRequestDTO request) {

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO messageResponseDTO = groupService.updateGroupRegisteredMembers(groupId, request, token);

		return ResponseEntity.ok(messageResponseDTO);
	}

	@PatchMapping("/{groupId}/externalmembers")
	public ResponseEntity<?> updateGroupExternalMembers(@PathVariable Long groupId,
			@RequestBody @Valid UpdateGroupExternalMembersRequestDTO request) {

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO messageResponseDTO = groupService.updateGroupExternalMembers(groupId, request, token);

		return ResponseEntity.ok(messageResponseDTO);
	}

	@DeleteMapping("/{groupId}")
	public ResponseEntity<?> deleteGroup(@PathVariable Long groupId,
										 @Valid @ModelAttribute DeleteGroupRequestDTO deleteGroupRequestDTO) {

		// The groupId is manually added to the DTO for validation.
		deleteGroupRequestDTO.setGroupId(groupId);

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO messageResponseDTO = groupService.deleteGroup(groupId, token);

		return ResponseEntity.ok(messageResponseDTO);
	}

	@DeleteMapping("/{groupId}/leave")
	public ResponseEntity<?> leaveGroup(@PathVariable Long groupId,
										@Valid @ModelAttribute DeleteGroupRequestDTO deleteGroupRequestDTO) {

		// The groupId is manually added to the DTO for validation.
		deleteGroupRequestDTO.setGroupId(groupId);

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO messageResponseDTO = groupService.leaveGroup(groupId, token);

		return ResponseEntity.ok(messageResponseDTO);
	}
}
