package com.copay.app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.copay.app.dto.PasswordUpdateRequest;
import com.copay.app.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	UserService userService;	
	
	@PutMapping("/{id}/password")
	public ResponseEntity<?> updatePassword(@PathVariable Long id,
	                                        @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest,
	                                        Principal principal) {
	    return userService.updatePassword(id, passwordUpdateRequest, principal);
	}
	
}
