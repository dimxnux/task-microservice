package com.internship.microservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class MicroserviceApplication {

	private static final Logger log = LoggerFactory.getLogger(MicroserviceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceApplication.class, args);
	}
}
