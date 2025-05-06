package com.copay.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.copay.app.entity.User;
import com.copay.app.service.FakeDataService;

import java.util.List;

@RestController
@RequestMapping("/api/fake-data")
public class FakeDataController {

	private final FakeDataService fakeDataService;

	public FakeDataController(FakeDataService fakeDataService) {
		this.fakeDataService = fakeDataService;
	}

	@PostMapping("/users")
	public ResponseEntity<List<User>> generateFakeUsers(@RequestParam(defaultValue = "10") int count) {

		return ResponseEntity.ok(fakeDataService.generateFakeUsers(count));
	}

	@DeleteMapping("/users")
	public ResponseEntity<String> clearUsers() {

		fakeDataService.clearFakeUsers();
		return ResponseEntity.ok("User data deleted");
	}

	@PostMapping("/groups")
	public ResponseEntity<List<User>> generateFakeGroups(@RequestParam(defaultValue = "10") int count) {

		return ResponseEntity.ok(fakeDataService.generateFakeUsers(count));
	}

	@DeleteMapping("/groups")
	public ResponseEntity<String> clearFakeGroups() {

		fakeDataService.clearFakeGroups();
		return ResponseEntity.ok("Group data deleted");
	}
}
