package com.copay.app.config.security;

import java.io.IOException;

import com.copay.app.exception.token.AccessRestrictedTokenException;
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

/**
 * This filter runs before any controller logic, so exceptions thrown here
 * won't be caught by GlobalExceptionHandler (@ControllerAdvice). 
 * For that reason, all errors must be manually handled within this filter.
 */
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

			// Validate the token through jwtService.
			jwtService.validateToken(token);

			// Check if the token is valid for the current route.
	        checkRegisterTokenValidity(token, request, response);
			
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

		// Custom exceptions caught here won't reach GlobalExceptionHandler.
		} catch (AccessRestrictedTokenException | InvalidTokenException e) {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"exception\": \"" + e.getClass().getSimpleName() + "\", \"message\": \"" + e.getMessage()
					+ "\", \"status\": 401}");
			return;
			
		// Generic exceptions - also won't reach GlobalExceptionHandler.
		} catch (Exception e) {

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			response.getWriter().write("{\"location\": \"JwtAuthenticationFilter try-catch block\", \"error\": \"Internal Server error, try again later\", \"message\": \"" + e.getMessage() + "\", \"status\": 500}");

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

	private void checkRegisterTokenValidity(String token, HttpServletRequest request, HttpServletResponse response) {
		if (Boolean.TRUE.equals(jwtService.isRegisterToken(token))) {
			String path = request.getServletPath();

			// Throws exception if "register" claim token is used outside step-two or password update endpoint.
			boolean isAllowed = path.equals("/api/auth/register/step-two") ||
					path.equals("/api/forgot-password-reset");

			if (!isAllowed) {
				throw new AccessRestrictedTokenException("Unauthorized - You need to be registered to access this resource.");
			}
		}
	}
}