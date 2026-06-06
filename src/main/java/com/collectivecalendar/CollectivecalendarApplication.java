package com.collectivecalendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableJpaRepositories(basePackages = "com.collectivecalendar.repository")
@EntityScan(basePackages = "com.collectivecalendar.model")
public class CollectivecalendarApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollectivecalendarApplication.class, args);
	}

}
