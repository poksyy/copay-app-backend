package com.copay.app.config.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

	@Bean
	public Flyway flyway() {
		// Load database configuration to Flyway through .env.
		return Flyway.configure().dataSource(System.getProperty("DB_URL"), System.getProperty("DB_USERNAME"),
				System.getProperty("DB_PASSWORD")).load();
	}
}
