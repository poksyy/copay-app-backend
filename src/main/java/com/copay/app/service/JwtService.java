package com.copay.app.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

	// JWT token duration in seconds (1 hour).
	private static final long JWT_EXPIRATION = 3600;

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
	
	public String generateToken(String phoneNumber) {
	    long expirationTimeMillis = System.currentTimeMillis() + (JWT_EXPIRATION * 1000);
	    return Jwts.builder()
	        .setSubject(phoneNumber)
	        .setIssuedAt(new Date())
	        .setExpiration(new Date(expirationTimeMillis))
	        .signWith(getSigningKey())
	        .compact();
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


	// Get username with JWT token.
	public String getUsernameFromToken(String token) {

		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	public long getExpirationTime() {
		return JWT_EXPIRATION;
	}
	
	public String extractPhoneNumber(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(jwtSecret.getBytes())
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }
}