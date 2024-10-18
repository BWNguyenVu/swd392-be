package com.example.myflower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MyflowerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyflowerApplication.class, args);
	}

}
