package com.praxedo.securefiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SecurefilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurefilesApplication.class, args);
	}

}
