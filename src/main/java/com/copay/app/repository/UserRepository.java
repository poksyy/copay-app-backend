package com.copay.app.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.copay.app.entity.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

	// findBy* → Retrieves the full entity if it exists.
	// existsBy* → Only checks for existence without loading data.

	// Finds a user by phone number, returning full entity if found.
	Optional<User> findByPhoneNumber(String phoneNumber);

	// Finds a user by email, returning full entity if found.
	Optional<User> findByEmail(String email);

	// Finds a user by username, returning full entity if found.
	Optional<User> findByUsername(String username);

	// Finds a user by Google ID, returning full entity if found.
	Optional<User> findByGoogleId(String googleId);

	// Checks if an email already exists without loading the entity.
	boolean existsByEmail(String email);

	// Checks if a phone number already exists without loading the entity.
	boolean existsByPhoneNumber(String phoneNumber);

	// Checks if a Google ID already exists without loading the entity.
	boolean existsByGoogleId(String googleId);

	// Deletes all users who have not completed the registration process.
	@Transactional
	@Modifying
	@Query("DELETE FROM User u WHERE u.createdAt < :now AND u.isCompleted = false")
	int deleteIncompleteUsers(@Param("now") LocalDateTime now);
}
