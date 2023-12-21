package com.GOBookingAPI.config;

import com.GOBookingAPI.security.Token.GoogleEntryPoint;
import com.GOBookingAPI.security.Token.GoogleFilter;
import com.GOBookingAPI.security.Token.GoogleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig implements WebMvcConfigurer{
	
	@Autowired
	GoogleEntryPoint entryPoint;
	
	@Autowired
	GoogleProvider provider;
	
//	@Autowired
//    private NonBlockInterceptor nonBlockInterceptor;

	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http ) throws Exception{
		
		
		http.cors();		// Kích hoạt CORS 
        http.csrf().disable();
		http.authorizeRequests().requestMatchers("/home/**", "/api/**" ,"/payment/**").permitAll();
		http.authorizeRequests().requestMatchers("/bookings/**").permitAll();
		http.authorizeRequests().requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll();
//		http.authorizeRequests().requestMatchers("/swagger-ui-custom.html").permitAll();
		http.authorizeRequests().requestMatchers("/**","/ws/**").authenticated().anyRequest().authenticated();
		http.exceptionHandling().authenticationEntryPoint(entryPoint);
		http.addFilterBefore(new GoogleFilter(), BasicAuthenticationFilter.class);
		http.authenticationProvider(provider);
		return http.build();
	}

//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(nonBlockInterceptor);
//	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
        .allowedOrigins("http://127.0.0.1:5500" , "http://127.0.0.1:3000" , "http://localhost:3000", "https://go-webapp.vercel.app", "https://forlorn-bite-production.up.railway.app", "http://forlorn-bite-production.up.railway.app")
        .allowedHeaders("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "PATCH")
        .maxAge(-1)   // add maxAge
        .allowCredentials(false);
	}
}
