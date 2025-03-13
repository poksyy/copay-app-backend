package com.copay.app.config.security;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.copay.app.service.CustomUserDetailsService;
import com.copay.app.service.JwtService;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	Dotenv dotenv = Dotenv.load();

	String jwtSecret = dotenv.get("JWT_SECRET");

	private final JwtService jwtService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	// Constructor to initialize JwtService and CustomAuthenticationEntryPoint
	// dependencies.
	public SecurityConfig(JwtService jwtService, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.jwtService = jwtService;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	// Bean to create a password encoder using BCrypt algorithm.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Bean to create a JWT decoder using a secret key for token verification.
	@Bean
	public JwtDecoder jwtDecoder() {
		byte[] secretBytes = Base64.getDecoder().decode(jwtSecret);
		SecretKeySpec key = new SecretKeySpec(secretBytes, "HMACSHA256");

		return NimbusJwtDecoder.withSecretKey(key).build();
	}

	// Bean to configure the security filter chain for HTTP requests.
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);

		// Disable CSRF protection.
		http.csrf(csrf -> csrf.disable())

				// Enable CORS with default settings.
				.cors(Customizer.withDefaults())

				// Allow public routes.
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/response", "/api/auth/register", "/api/auth/login", "/api/users/**")
						.permitAll()

						// Require authentication for all other endpoints.
						.anyRequest().authenticated())

				// Set session policy to stateless.
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Custom error for unauthenticated requests.
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(customAuthenticationEntryPoint))

				// Enable JWT-based OAuth2 resource server.
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))

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