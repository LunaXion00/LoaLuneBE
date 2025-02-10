package com.example.lunaproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LunaprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunaprojectApplication.class, args);
	}

}
