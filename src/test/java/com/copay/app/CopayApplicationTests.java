package com.copay.app;

import com.copay.app.config.env.EnvLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * CopayApplicationTests
 * ----------------------
 * This test class is responsible for validating that the Spring application context
 * can be loaded successfully. It uses the @SpringBootTest annotation to bootstrap
 * the entire application context, simulating a real runtime environment.
 *
 * Environment Setup:
 * ------------------
 * The setupEnv() method is annotated with @BeforeAll and ensures that environment
 * variables are loaded before the context initialization. This is necessary because
 * the application relies on environment variables defined in a .env file, which are
 * normally loaded in the main() method via EnvLoader.
 *
 * Since the main() method is not invoked during test execution, the variables must be
 * explicitly loaded to avoid context failures due to missing configuration.
 */
@SpringBootTest
class CopayApplicationTests {

	@BeforeAll
	static void setupEnv() {
		EnvLoader.loadEnvVariables();
	}
	@Test
	void contextLoads() {
	}
}
