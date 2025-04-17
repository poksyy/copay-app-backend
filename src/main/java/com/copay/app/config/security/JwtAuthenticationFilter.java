package com.copay.app.config.security;

import java.io.IOException;

import com.copay.app.exception.token.InvalidTokenException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.copay.app.service.CustomUserDetailsService;
import com.copay.app.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final CustomUserDetailsService customUserDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
		this.jwtService = jwtService;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {

			JwtService.TokenValidationContext context = inferContextFromRequest(request);
			JwtService.setCurrentContext(context);

			// Get the Authorization header.
			String authorizationHeader = request.getHeader("Authorization");

			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				filterChain.doFilter(request, response);
				return;
			}

			// Extract the token and remove "Bearer " prefix.
			String token = authorizationHeader.substring(7);

			// Validate the token trough jwtService.
			jwtService.validateToken(token);

			// Get the identifier (phone number or email).
			String userIdentifier = jwtService.getUserIdentifierFromToken(token);

			UserDetails userDetails;

			// Load user details using CustomUserDetailsService.
			userDetails = customUserDetailsService.loadUserByUsername(userIdentifier);

			// Creates an authentication token using the retrieved user details.
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, token, userDetails.getAuthorities());

			// Set authentication in the security context.
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		} catch (InvalidTokenException e) {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"" + e.getMessage() + "\", \"message\": \"" + e.getMessage()
					+ "\", \"status\": 401}");
			return;

		} catch (Exception e) {

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": JwtAuthenticationFilter try catch block\"" + "Internal Server error, try again later" + "\", \"message\": \"" + e.getMessage()
			+ "\", \"status\": 500}");
			return;
		} finally {

			JwtService.clearContext();
		}

		// Continue with the request.
		filterChain.doFilter(request, response);
	}

	private JwtService.TokenValidationContext inferContextFromRequest(HttpServletRequest request) {
		String path = request.getServletPath();

		if (path.contains("/auth/logout")) {
			return JwtService.TokenValidationContext.LOGOUT;
		} else if (path.contains("/auth/register/step-two")) {
			return JwtService.TokenValidationContext.REGISTER_STEP_TWO;
		}

		return JwtService.TokenValidationContext.DEFAULT;
	}

}