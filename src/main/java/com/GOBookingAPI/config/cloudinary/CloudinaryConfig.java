package com.GOBookingAPI.config.cloudinary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

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
}
