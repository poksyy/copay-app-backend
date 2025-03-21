package com.copay.app.config.env;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {

	public static void loadEnvVariables() {
		// Load the .env file and avoid errors if it's missing.
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Define the .env variables as a System Properties.
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
	}
}
