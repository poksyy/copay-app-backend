package com.copay.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class GoogleAuthConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        // Google's JWKS (JSON Web Key Set) URI for verifying ID tokens
        String jwkSetUri = "https://www.googleapis.com/oauth2/v3/certs";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}