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

	@PostMapping("/reset-group-members")
	public String resetGroupMembersTable() {

		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("TRUNCATE TABLE group_members");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

		return "Group members reset successful!";
	}

	@PostMapping("/reset-external-members")
	public String resetExternalMembersTable() {

		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("TRUNCATE TABLE external_members");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

		return "External members reset successful!";
	}

	@PostMapping("/reset-groups")
	public String resetGroupsTable() {

		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("TRUNCATE TABLE `groups`");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

		return "Groups reset successful!";
	}

	@PostMapping("/reset-payment-confirmations")
	public String resetPaymentConfirmationsTable() {
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("TRUNCATE TABLE payment_confirmations");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

		return "Payment confirmations reset successful!";
	}

	@PostMapping("/reset-user-expenses")
	public String resetUserExpensesTable() {
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("TRUNCATE TABLE user_expenses");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

		return "User expenses reset successful!";
	}

	@PostMapping("/reset-expenses")
	public String resetExpensesTable() {
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("TRUNCATE TABLE expenses");
		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

		return "Expenses reset successful!";
	}
}