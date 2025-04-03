package com.copay.app.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {
	private final JdbcTemplate jdbcTemplate;

	public DatabaseController(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@GetMapping("/response")
	public String connectionTestResponse() {
		return "Connection successful!";
	}

	@PostMapping("/reset-users")
	public String resetUsersTable() {

		jdbcTemplate.execute("ALTER TABLE users ADD UNIQUE (phone_number)");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("TRUNCATE TABLE users");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

		return "User reset successful!";
	}
}