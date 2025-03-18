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
	public ResponseEntity<List<User>> generateUsers(@RequestParam(defaultValue = "10") int count) {
		return ResponseEntity.ok(fakeDataService.generateFakeUsers(count));
	}

	@DeleteMapping("/clear-users")
	public ResponseEntity<String> clearUsersFakeData() {
		fakeDataService.clearUsersFakeData();
		return ResponseEntity.ok("Fake data deleted");
	}
}
