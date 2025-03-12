package com.copay.app.repository;

import com.copay.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
	Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}