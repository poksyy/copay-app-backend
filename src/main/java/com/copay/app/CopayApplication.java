package com.copay.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.copay.app.config.env.EnvLoader;

@SpringBootApplication
public class CopayApplication {

    public static void main(String[] args) {
        // Load the .env file through EnvLoad class.
        EnvLoader.loadEnvVariables();

        // Start the Spring Boot application.
        SpringApplication.run(CopayApplication.class, args);
    }
}
