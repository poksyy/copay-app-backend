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
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;

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
	            .requestMatchers("/api/response", "/api/auth/register", "/api/auth/login", "/api/users/**").permitAll() // Allow public routes
	            .anyRequest().authenticated()) // Any other requests require authentication
	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Disable session management 
			// Custom error message for unauthenticated requests.
			.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(customAuthenticationEntryPoint))
			// Enable JWT-based OAuth2 resource server support.
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
			// Enable CORS (Cross-Origin Resource Sharing) with default settings
	        .cors(Customizer.withDefaults()) 
			// Add the JWT filter before the default authentication filter.
	        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); 

		// Return the configured SecurityFilterChain
	    return http.build(); 
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
