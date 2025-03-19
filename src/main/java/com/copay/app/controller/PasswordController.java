package com.copay.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.PasswordUpdateRequest;
import com.copay.app.service.password.PasswordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

	@Autowired
	private PasswordService passwordService;

	@PutMapping("/{id}/reset-password")
	public ResponseEntity<?> resetPassword(@PathVariable Long id,
			@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest,
			@RequestHeader("Authorization") String authorizationHeader) {

		// Extract the token from the Authorization header
		String token = authorizationHeader.replace("Bearer ", "");

		return passwordService.resetPassword(id, passwordUpdateRequest, token);
	}
}
