package com.copay.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CopayApplication {

    public static void main(String[] args) {
    	
        // Load .env file and set system properties before Spring Boot starts
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Set system properties before Spring Boot initialization
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        // Run Spring Boot application
        SpringApplication.run(CopayApplication.class, args);
        
    }
}
