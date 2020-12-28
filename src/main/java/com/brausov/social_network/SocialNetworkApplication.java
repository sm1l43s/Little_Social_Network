package com.brausov.social_network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.thymeleaf.templateresolver.ITemplateResolver;

@SpringBootApplication
public class SocialNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

}
