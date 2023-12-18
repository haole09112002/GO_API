package com.GOBookingAPI.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApiConfig {
    @Value("${cloudinary.api.key}")
    private String apiKey;

    @Value("${cloudinary.api.name}")
    private String name;

    @Value("${cloudinary.api.secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name",this.name);
        config.put("api_key", this.apiKey);
        config.put("api_secret", this.apiSecret);
        return new Cloudinary(config);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
