package com.copay.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.group.CreateGroupRequestDTO;
import com.copay.app.dto.group.DeleteGroupRequestDTO;
import com.copay.app.dto.group.GetGroupRequestDTO;
import com.copay.app.dto.group.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.responses.CreateGroupResponseDTO;
import com.copay.app.dto.responses.DeleteGroupResponseDTO;
import com.copay.app.dto.responses.GetGroupResponseDTO;
import com.copay.app.dto.responses.UpdateGroupResponseDTO;
import com.copay.app.service.ValidationService;
import com.copay.app.service.group.GroupServiceImpl;
import com.copay.app.validation.ValidationErrorResponse;

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
	public ResponseEntity<?> createGroup(@RequestBody @Valid CreateGroupRequestDTO createGroupRequestDTO,
			BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations.
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		System.err.println("Create group inputs from front" + createGroupRequestDTO);

		CreateGroupResponseDTO createGroupResponseDTO = groupService.createGroup(createGroupRequestDTO);

		return ResponseEntity.ok(createGroupResponseDTO);
	}

	// Endpoint to retrieve groups for a given user (HomeScreen display).
	@GetMapping("/{userId}")
	public ResponseEntity<?> getGroupsByUser(@PathVariable Long userId,
			@Valid @ModelAttribute GetGroupRequestDTO getGroupRequestDTO, BindingResult result) {

		// The userId is manually added to the DTO for validation.
		getGroupRequestDTO.setUserId(userId);

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations.
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		GetGroupResponseDTO getGroupResponseDTO = groupService.getGroupsByUserId(userId);

		return ResponseEntity.ok(getGroupResponseDTO);
	}

	@DeleteMapping("/{groupId}")
	public ResponseEntity<?> deleteGroup(@PathVariable Long groupId,
			@Valid @ModelAttribute DeleteGroupRequestDTO deleteGroupRequestDTO, BindingResult result) {

		// The groupId is manually added to the DTO for validation.
		deleteGroupRequestDTO.setGroupId(groupId);

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations.
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Get the token from the SecurityContextHolder (set by
		// JwtAuthenticationFilter).
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		DeleteGroupResponseDTO response = groupService.deleteGroup(groupId, token);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{groupId}/leave")
	public ResponseEntity<?> leaveGroup(@PathVariable Long groupId,
			@Valid @ModelAttribute DeleteGroupRequestDTO deleteGroupRequestDTO, BindingResult result) {

		// The groupId is manually added to the DTO for validation.
		deleteGroupRequestDTO.setGroupId(groupId);

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations.
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		// Get the token from the SecurityContextHolder (set by JwtAuthenticationFilter)
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		UpdateGroupResponseDTO response = groupService.leaveGroup(groupId, token);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{groupId}")
	public ResponseEntity<?> updateGroup(@PathVariable Long groupId, @RequestBody Map<String, Object> fieldChanges) {

		UpdateGroupResponseDTO response = groupService.updateGroup(groupId, fieldChanges);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{groupId}/copaymembers")
	public ResponseEntity<?> updateGroupCopayMembers(@PathVariable Long groupId,
			@RequestBody @Valid UpdateGroupRegisteredMembersRequestDTO request, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations.
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		UpdateGroupResponseDTO response = groupService.updateGroupRegisteredMembers(groupId, request);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{groupId}/externalmembers")
	public ResponseEntity<?> updateGroupExternalMembers(@PathVariable Long groupId,
			@RequestBody @Valid UpdateGroupExternalMembersRequestDTO request, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations.
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		UpdateGroupResponseDTO response = groupService.updateGroupExternalMembers(groupId, request);

		return ResponseEntity.ok(response);
	}
}
