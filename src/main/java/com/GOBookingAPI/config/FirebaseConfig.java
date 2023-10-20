package com.GOBookingAPI.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FirebaseConfig {

	@Value("${firebase.config.path}")
	private String configPath;
	
	
	@PostConstruct
	public void init() throws IOException{
		ClassPathResource resource = new ClassPathResource(configPath);
		
		FirebaseOptions options = FirebaseOptions.builder()
			    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
			    .build();

			FirebaseApp.initializeApp(options);
			log.info("App name: {}" , FirebaseApp.getInstance().getName());
	}
}
