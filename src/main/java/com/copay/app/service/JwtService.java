package com.copay.app.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.copay.app.entity.RevokedToken;
import com.copay.app.repository.RevokedTokenRepository;
import com.copay.app.exception.token.InvalidTokenException;

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

	// Context enum for token validation scenarios.
	public enum TokenValidationContext {
		LOGOUT,
		REGISTER_STEP_TWO,
		DEFAULT
	}

	// Holder for context thread.
	private static final ThreadLocal<TokenValidationContext> contextHolder = new ThreadLocal<>();

	// Constructor
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

	// Set current validation token context.
	public static void setCurrentContext(TokenValidationContext context) {
		contextHolder.set(context);
	}

	// Usa the actual context from ThreadLocal or use DEFAULT if it does not exist.
	public static TokenValidationContext getCurrentContext() {
		TokenValidationContext context = contextHolder.get();
		return context != null ? context : TokenValidationContext.DEFAULT;
	}

	// Clear the actual context
	public static void clearContext() {
		contextHolder.remove();
	}

	// Validate JWT token with explicit context parameter.
	public boolean validateToken(String token, TokenValidationContext context) {
		System.out.println("Debug: Validation context received - " + context);

		// Check if token is revoked
		if (revokedTokenRepository.existsByToken(token)) {
			System.err.println("Trying to do HTTP requests with revoked token in context: " + context);

			String errorMessage = switch (context) {
				case LOGOUT -> "Session was already terminated";
				case REGISTER_STEP_TWO -> "Registration token has expired, please start the process again";
				default -> "This token has already been revoked";
			};

			throw new InvalidTokenException(errorMessage);
		}

		try {
			// Parse and validate the token.
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;

		} catch (ExpiredJwtException e) {
			throw new InvalidTokenException(getExpirationMessage(context));
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			throw new InvalidTokenException("Invalid token format");
		}
	}

	// Original validateToken method now uses the ThreadLocal context.
	public boolean validateToken(String token) {
		TokenValidationContext context = getCurrentContext();
		System.out.println("Debug: Using ThreadLocal context - " + context);
		return validateToken(token, context);
	}

	// Helper method to get context-specific expiration message.
	private String getExpirationMessage(TokenValidationContext context) {
		return context == TokenValidationContext.REGISTER_STEP_TWO
				? "Your registration time has expired, please try again from the beginning"
				: "Your session has expired, please login again";
	}

	// Generate 1 hour token for the registerStepTwo().
	public String generateToken(String phoneNumber) {
		long expirationTimeMillis = System.currentTimeMillis() + (REGULAR_JWT_EXPIRATION * 1000);
		return Jwts.builder()
				.setSubject(phoneNumber)
				.setIssuedAt(new Date())
				.setExpiration(new Date(expirationTimeMillis))
				.signWith(getSigningKey())
				.compact();
	}

	// Generate temporal 5 minutes token for the registerStepOne().
	public String generateTemporaryToken(String email) {
		long expirationTimeMillis = System.currentTimeMillis() + (TEMPORAL_JWT_EXPIRATION * 1000);
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date(expirationTimeMillis))
				.signWith(getSigningKey())
				.compact();
	}

	// Identify the user through the token.
	public String getUserIdentifierFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
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