package com.example.satto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class SattoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SattoApplication.class, args);
	}

}
