package com.copay.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CopayApplication {

    public static void main(String[] args) {
    	
        // Load the .env file and ignore it if missing (prevents errors in non-local environments)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Set database and security environment variables as system properties
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        
        // Start the Spring Boot application
        SpringApplication.run(CopayApplication.class, args);
    }
}
