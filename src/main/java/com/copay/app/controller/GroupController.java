package com.copay.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.group.GroupJoinRequestDTO;
import com.copay.app.dto.group.GroupRequestDTO;
import com.copay.app.dto.responses.GroupResponseDTO;
import com.copay.app.service.GroupService;
import com.copay.app.service.ValidationService;
import com.copay.app.validation.ValidationErrorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

	@Autowired
	private GroupService groupService;

	// Endpoint to create a new group
	@PostMapping
	public ResponseEntity<?> createGroup(@RequestBody @Valid GroupRequestDTO groupRequestDTO, BindingResult result) {

		ValidationErrorResponse validationResponse = ValidationService.validate(result);
		
		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}

		GroupResponseDTO responseDTO = groupService.createGroup(groupRequestDTO);
		return ResponseEntity.ok(responseDTO);
	}

	// Endpoint to retrieve groups for a given user (for HomeScreen display).
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getGroupsByUser(@PathVariable Long userId) {

		List<GroupResponseDTO> responseDTOs = groupService.getGroupsByUser(userId);
		return ResponseEntity.ok(responseDTOs);
	}

	// Endpoint to join a group.
	@PostMapping("/{groupId}/join")
	public ResponseEntity<?> joinGroup(@PathVariable Long groupId, @RequestBody @Valid GroupJoinRequestDTO joinRequest,
			BindingResult result) {
		
		ValidationErrorResponse validationResponse = ValidationService.validate(result);
		
		// Validates the DTO annotations. 
		if (validationResponse != null) {
			return ResponseEntity.badRequest().body(validationResponse);
		}
		
		GroupResponseDTO responseDTO = groupService.joinGroup(groupId, joinRequest.getUserId());
		return ResponseEntity.ok(responseDTO);
	}
}
