package com.copay.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.copay.app.service.CustomUserDetailsService;
import com.copay.app.service.JwtService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtService jwtService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	// Constructor to initialize JwtService and CustomAuthenticationEntryPoint
	// dependencies.
	public SecurityConfig(JwtService jwtService, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.jwtService = jwtService;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	// Bean to create a password encoder using BCrypt algorithm.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Bean to configure the security filter chain for HTTP requests.
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService,
				customUserDetailsService);

		// Disable CSRF protection.
		http.csrf(csrf -> csrf.disable())
				// Enable CORS with default settings.
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/response", "/api/auth/register/step-one", "/api/auth/login",
								"/api/users/**", "/api/fake-data/**", "/reset-password.html", "/api/forgot-password",
								"/api/forgot-password-reset", "/static/**", "/api/groups", "/api/groups/**",
								"/api/db/**")
						// Allow public routes.
						.permitAll()
						// Require authentication for all other endpoints.
						.anyRequest().authenticated())
				// Stateless sessions.
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// Custom error for unauthenticated requests.
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(customAuthenticationEntryPoint))
				// Add JWT filter before authentication filter.
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// Bean to create the AuthenticationManager to authenticate users.
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);

		// Configures AuthenticationManager to load user details and verify password.
		authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());

		// Return the AuthenticationManager.
		return authenticationManagerBuilder.build();
	}
}
