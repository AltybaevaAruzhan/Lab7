package com.example.taskApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskApplication.class, args);
		PasswordEncoder encoder = new BCryptPasswordEncoder();

		String rawPassword = "aruka";

		// Hash the password
		String firstHash = "$2a$10$lC.5ABMINrXF.yj4oeqPde1kTE/TDnlNoik0WZ7FDzvU6QoVGu3SO";
		String secondHash = encoder.encode(rawPassword);

		// Print both hashes (they will be different)
		System.out.println("First Hash: " + firstHash);
		System.out.println("Second Hash: " + secondHash);

		// Check if the raw password matches the first hash
		boolean matchesFirst = encoder.matches(rawPassword, firstHash);
		System.out.println("Matches First Hash: " + matchesFirst);

		// Check if the raw password matches the second hash
		boolean matchesSecond = encoder.matches(rawPassword, secondHash);
		System.out.println("Matches Second Hash: " + matchesSecond);

	}

}
