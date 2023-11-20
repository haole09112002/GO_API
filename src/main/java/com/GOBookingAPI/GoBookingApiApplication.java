package com.GOBookingAPI;


import com.GOBookingAPI.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class GoBookingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoBookingApiApplication.class, args);
	}

}
