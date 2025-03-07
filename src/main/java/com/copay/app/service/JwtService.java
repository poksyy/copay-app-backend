package com.copay.app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	// .env JWT key.
	private String jwtSecret;

	// JWT token duration in seconds (1 hour).
	private static final long JWT_EXPIRATION = 3600;

	// Transform String to secret key.
	private SecretKey getSigningKey() {

		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	// Generate JWT token.
	public String generateToken(String username) {

		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)).signWith(getSigningKey())
				.compact();
	}

	// Validate JWT token.
	public boolean validateToken(String token) {

		try {

			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;

		} catch (Exception e) {

			return false;
		}
	}

	// Get username with JWT token.
	public String getUsernameFromToken(String token) {

		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	public long getExpirationTime() {
		return JWT_EXPIRATION;
	}
}