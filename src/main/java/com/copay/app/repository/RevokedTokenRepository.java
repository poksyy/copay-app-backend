package com.copay.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.copay.app.entity.RevokedToken;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    Optional<RevokedToken> findByToken(String token);

    boolean existsByToken(String token);
}