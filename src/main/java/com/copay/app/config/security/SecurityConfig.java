package com.copay.app.config.security;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.copay.app.service.JwtService;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	Dotenv dotenv = Dotenv.load();
	String jwtSecret = dotenv.get("JWT_SECRET");

    private final JwtService jwtService;
    private final UserDetailServiceImpl userDetailServiceImpl;

    public SecurityConfig(JwtService jwtService, UserDetailServiceImpl userDetailServiceImpl) {
        this.jwtService = jwtService;
        this.userDetailServiceImpl = userDetailServiceImpl;
    }
    
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
	    // Create an instance of the JwtAuthenticationFilter with the JwtService
	    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
	    
	    http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/api/response","/api/auth/register", "/api/auth/login").permitAll() // Allow public routes
	            .anyRequest().authenticated()) // Any other requests require authentication
	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Disable session management 
	        .cors(Customizer.withDefaults()) // Enable CORS (Cross-Origin Resource Sharing) with default settings
	        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add the JWT filter before the default authentication filter

	    return http.build(); // Return the configured SecurityFilterChain
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, 
	                                                   PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setPasswordEncoder(passwordEncoder);
	    authProvider.setUserDetailsService(userDetailsService);

	    return new ProviderManager(authProvider);
	}
}
