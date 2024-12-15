package com.example.taskApplication;

import com.example.taskApplication.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TaskApplication {


	public static void main(String[] args) {
		SpringApplication.run(TaskApplication.class, args);

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String p = "adelya";
		System.out.println(passwordEncoder.encode(p));

	}

}
