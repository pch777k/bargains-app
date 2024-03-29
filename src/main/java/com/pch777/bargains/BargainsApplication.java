package com.pch777.bargains;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class BargainsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BargainsApplication.class, args);
	}
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}
	
	@Bean
	public Random random() {
		return new Random();
	}

}
