package com.copay.app.config.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.copay.app.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// Retrieve the Authorization header from the incoming request.
		String authorizationHeader = request.getHeader("Authorization");

		// Check if the Authorization header is present and starts with "Bearer".
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			// Extract the token by removing the "Bearer " prefix. IMPORTANT.
			String token = authorizationHeader.substring(7);

			// If the token is valid, authenticate the user.
			if (jwtService.validateToken(token)) {
				// Extract the username from the token.
				String username = jwtService.getUsernameFromToken(token);
				// Create an authentication token with the username (null for credentials and
				// authorities for now).
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						username, null, null);

				// Set the authentication object in the SecurityContextHolder.
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}

		// Continue with the filter chain (pass request and response to the next filter.
		// or handler)
		filterChain.doFilter(request, response);
	}
}
