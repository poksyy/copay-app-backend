package com.copay.app.config.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FlywayRunner implements CommandLineRunner {

	private final Flyway flyway;

	// Constructor with dependency injection.
	public FlywayRunner(Flyway flyway) {
		this.flyway = flyway;
	}

	@Override
	public void run(String... args) throws Exception {
		// Commented --> No Flyway migrations on server start.
		// Uncommented --> Flyway migrations will run on server start.
		// flyway.migrate();
	}
}