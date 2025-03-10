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

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	Dotenv dotenv = Dotenv.load();

	String jwtSecret = dotenv.get("JWT_SECRET");

	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		byte[] secretBytes = Base64.getDecoder().decode(jwtSecret);
		SecretKeySpec key = new SecretKeySpec(secretBytes, "HMACSHA256");

		return NimbusJwtDecoder.withSecretKey(key).build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/response").permitAll()
						.requestMatchers("/api/auth/register").permitAll().requestMatchers("/api/auth/login")
						.permitAll().anyRequest().authenticated())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
				.cors(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);

		// Configures AuthenticationManager to load user details and verify password.
		authenticationManagerBuilder.userDetailsService(userDetailServiceImpl).passwordEncoder(passwordEncoder());

		// Return the AuthenticationManager.
		return authenticationManagerBuilder.build();
	}
}
