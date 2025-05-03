package com.copay.app.controller;

import java.util.Map;

import com.copay.app.dto.MessageResponseDTO;
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

import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.DeleteGroupRequestDTO;
import com.copay.app.dto.group.request.GetGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.response.CreateGroupResponseDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;
import com.copay.app.service.group.GroupServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

	private final GroupServiceImpl groupService;

	// Constructor
    public GroupController(GroupServiceImpl groupService) {
        this.groupService = groupService;
    }

    // Endpoint to create a new group.
	@PostMapping
	public ResponseEntity<?> createGroup(@RequestBody @Valid CreateGroupRequestDTO createGroupRequestDTO) {

		System.err.println("Create group inputs from front" + createGroupRequestDTO);

		CreateGroupResponseDTO createGroupResponseDTO = groupService.createGroup(createGroupRequestDTO);

		return ResponseEntity.ok(createGroupResponseDTO);
	}

	// Endpoint to retrieve groups for a given user (HomeScreen display).
	@GetMapping("/{userId}")
	public ResponseEntity<?> getGroupsByUser(@PathVariable Long userId,
			@Valid @ModelAttribute GetGroupRequestDTO getGroupRequestDTO) {

		// The userId is manually added to the DTO for validation.
		getGroupRequestDTO.setUserId(userId);

		GetGroupResponseDTO getGroupResponseDTO = groupService.getGroupsByUserId(userId);

		return ResponseEntity.ok(getGroupResponseDTO);
	}

	@DeleteMapping("/{groupId}")
	public ResponseEntity<?> deleteGroup(@PathVariable Long groupId,
			@Valid @ModelAttribute DeleteGroupRequestDTO deleteGroupRequestDTO) {

		// The groupId is manually added to the DTO for validation.
		deleteGroupRequestDTO.setGroupId(groupId);

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO response = groupService.deleteGroup(groupId, token);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{groupId}/leave")
	public ResponseEntity<?> leaveGroup(@PathVariable Long groupId,
			@Valid @ModelAttribute DeleteGroupRequestDTO deleteGroupRequestDTO) {

		// The groupId is manually added to the DTO for validation.
		deleteGroupRequestDTO.setGroupId(groupId);

		// Get the token from the SecurityContextHolder.
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO response = groupService.leaveGroup(groupId, token);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{groupId}")
	public ResponseEntity<?> updateGroup(@PathVariable Long groupId, @RequestBody Map<String, Object> fieldChanges) {

		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO response = groupService.updateGroup(groupId, fieldChanges, token);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{groupId}/copaymembers")
	public ResponseEntity<?> updateGroupCopayMembers(@PathVariable Long groupId,
			@RequestBody @Valid UpdateGroupRegisteredMembersRequestDTO request) {

		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO response = groupService.updateGroupRegisteredMembers(groupId, request, token);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{groupId}/externalmembers")
	public ResponseEntity<?> updateGroupExternalMembers(@PathVariable Long groupId,
			@RequestBody @Valid UpdateGroupExternalMembersRequestDTO request) {

		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		MessageResponseDTO response = groupService.updateGroupExternalMembers(groupId, request, token);

		return ResponseEntity.ok(response);
	}
}
