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

import com.GOBookingAPI.security.Token.FirebaseEntryPoint;
import com.GOBookingAPI.security.Token.FirebaseFilter;
import com.GOBookingAPI.security.Token.FirebaseProvider;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Autowired
	FirebaseEntryPoint entryPoint;
	
	@Autowired
	FirebaseProvider provider;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http.cors().and() // Kích hoạt CORS
        .csrf().disable();
		
		http.authorizeRequests().requestMatchers("/account/register").authenticated().anyRequest().authenticated();
		http.exceptionHandling().authenticationEntryPoint(entryPoint);
		http.addFilterBefore(new FirebaseFilter(), BasicAuthenticationFilter.class);
		http.authenticationProvider(provider);
		return http.build();
	}
}
