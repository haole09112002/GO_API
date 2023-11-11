package com.GOBookingAPI.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.GOBookingAPI.security.Token.GoogleEntryPoint;
import com.GOBookingAPI.security.Token.GoogleFilter;
import com.GOBookingAPI.security.Token.GoogleProvider;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Autowired
	GoogleEntryPoint entryPoint;
	
	@Autowired
	GoogleProvider provider;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		

		http.cors().and() // Kích hoạt CORS 
        .csrf().disable();
		http.authorizeRequests().requestMatchers("/home/**","/ws/**").permitAll();
		http.authorizeRequests().requestMatchers("/bookings/**").permitAll();
		http.authorizeRequests().requestMatchers("/").authenticated().anyRequest().authenticated();
		http.exceptionHandling().authenticationEntryPoint(entryPoint);
		http.addFilterBefore(new GoogleFilter(), BasicAuthenticationFilter.class);
		http.authenticationProvider(provider);
		return http.build();
	}
}
