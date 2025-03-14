package com.copay.app.repository;

import com.copay.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	// findBy* → Retrieves the full entity if it exists.
	// existsBy* → Only checks for existence without loading data.

	// Finds a user by phone number, returning full entity if found.
	Optional<User> findByPhoneNumber(String phoneNumber);

	// Finds a user by email, returning full entity if found.
	Optional<User> findByEmail(String email);

	// Finds a user by username, returning full entity if found.
	Optional<User> findByUsername(String username);

	// Checks if an email already exists without loading the entity.
	boolean existsByEmail(String email);

	// Checks if a phone number already exists without loading the entity.
	boolean existsByPhoneNumber(String phoneNumber);
}
