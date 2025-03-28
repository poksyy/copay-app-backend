package com.copay.app.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.copay.app.entity.RevokedToken;
import com.copay.app.repository.RevokedTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	// .env JWT key.
	private String jwtSecret;

	private final RevokedTokenRepository revokedTokenRepository;

	// JWT token duration in seconds (1 hour).
	public static final long REGULAR_JWT_EXPIRATION = 3600;

	// JWT Temporal Token (5 minutes).
	public static final long TEMPORAL_JWT_EXPIRATION = 5 * 60;

	@Autowired
	public JwtService(RevokedTokenRepository revokedTokenRepository) {
		this.revokedTokenRepository = revokedTokenRepository;
	}

	// Transform String to secret key.
	private SecretKey getSigningKey() {

		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	@PostConstruct
	public void init() {

		if (jwtSecret == null || jwtSecret.isEmpty()) {

			throw new IllegalStateException("JWT Secret key is not configured in the environment.");
		}
	}

	// Validate JWT token.
	public boolean validateToken(String token) {

		try {

			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;

		} catch (ExpiredJwtException e) {

			// Expired token
			System.out.println("Token expired");
			return false;

		} catch (UnsupportedJwtException e) {

			// Unsupported token
			System.out.println("Unsupported token");
			return false;

		} catch (MalformedJwtException e) {

			// Malformed token
			System.out.println("Malformed token");
			return false;

		} catch (SignatureException e) {

			// Invalid signature
			System.out.println("Invalid signature");
			return false;

		} catch (Exception e) {
			// General catch-all exception
			return false;
		}
	}

	// Generate 1 hour token for the registerStepTwo()
	public String generateToken(String phoneNumber) {

		long expirationTimeMillis = System.currentTimeMillis() + (REGULAR_JWT_EXPIRATION * 1000);

		return Jwts.builder().setSubject(phoneNumber).setIssuedAt(new Date())
				.setExpiration(new Date(expirationTimeMillis)).signWith(getSigningKey()).compact();
	}

	// Genereate temporal 5 minutes token for the registerStepOne().
	public String generateTemporaryToken(String email) {

		long expirationTimeMillis = System.currentTimeMillis() + (TEMPORAL_JWT_EXPIRATION * 1000);

		return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(new Date(expirationTimeMillis))
				.signWith(getSigningKey()).compact();
	}

	// Identify the user through the token.
	public String getUserIdentifierFromToken(String token) {

		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	public long getExpirationTime(boolean isTemporary) {

		if (isTemporary) {

			return REGULAR_JWT_EXPIRATION;

		} else {

			return TEMPORAL_JWT_EXPIRATION;
		}
	}

	// Method to revoke token.
	public void revokeToken(String token) {
		
		RevokedToken revokedToken = new RevokedToken();
		
		revokedToken.setToken(token);
		revokedTokenRepository.save(revokedToken);
	}
}