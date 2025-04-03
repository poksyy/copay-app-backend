package com.copay.app.config.env;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Objects;

public class EnvLoader {

	public static void loadEnvVariables() {

		// Load the .env file and avoid errors if it's missing.
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Define the .env variables as a System Properties.
		// Database
		System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
		System.setProperty("DB_USERNAME", Objects.requireNonNull(dotenv.get("DB_USERNAME")));
		System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));

		// JWT
		System.setProperty("JWT_SECRET", Objects.requireNonNull(dotenv.get("JWT_SECRET")));

		// Mail
		System.setProperty("MAIL_URL", Objects.requireNonNull(dotenv.get("MAIL_URL")));
		System.setProperty("MAIL_USERNAME", Objects.requireNonNull(dotenv.get("MAIL_USERNAME")));
		System.setProperty("MAIL_PASSWORD", Objects.requireNonNull(dotenv.get("MAIL_PASSWORD")));
	}
}
