package com.copay.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.group.GetGroupRequestDTO;
import com.copay.app.dto.group.CreateGroupRequestDTO;
import com.copay.app.dto.group.DeleteGroupRequestDTO;
import com.copay.app.dto.responses.CreateGroupResponseDTO;
import com.copay.app.dto.responses.DeleteGroupResponseDTO;
import com.copay.app.dto.responses.GetGroupResponseDTO;
import com.copay.app.service.GroupService;
import com.copay.app.service.ValidationService;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

	@Autowired
	private GroupService groupService;

	// Endpoint to create a new group.
	@PostMapping
	public ResponseEntity<?> createGroup(@RequestBody @Valid CreateGroupRequestDTO groupRequestDTO,
			BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);

		// Validates the DTO annotations.
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		System.err.println(groupRequestDTO);

		CreateGroupResponseDTO createGroupResponseDTO = groupService.createGroup(groupRequestDTO);

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

		// Get the token from the SecurityContextHolder (set by JwtAuthenticationFilter)
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

		DeleteGroupResponseDTO response = groupService.deleteGroup(groupId, token);

		return ResponseEntity.ok(response);
	}
}
