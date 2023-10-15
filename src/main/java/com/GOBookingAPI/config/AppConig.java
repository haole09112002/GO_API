package com.GOBookingAPI.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.GOBookingAPI.services.Oauth2.Security.CustomOAuth2UserDetailService;
import com.GOBookingAPI.services.Oauth2.Security.handler.CustomOAuth2FailtureHandler;
import com.GOBookingAPI.services.Oauth2.Security.handler.CustomOAuth2SuccessHandler;
import com.GOBookingAPI.services.security.UserDetailsServiceCustom;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class AppConig  {
	
	@Autowired
	private CustomOAuth2UserDetailService customOAuth2UserDetailService;
	
	@Autowired
	private CustomOAuth2FailtureHandler customOAuth2FailtureHandler;
	
	@Autowired
	private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceCustom();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
		
		builder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
		
		AuthenticationManager manager = builder.build();
		http.cors().disable()
		.csrf().disable()
		.formLogin().disable()
		.authorizeHttpRequests()
		.requestMatchers("/account/**").permitAll()
		.requestMatchers("/customer/**").hasAnyAuthority("CUSTOMER")
		.requestMatchers("/driver/**").hasAnyAuthority("DRIVER")
		.requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
		.anyRequest().authenticated()
		.and()
        .formLogin()
        .loginPage("/login")
        .loginProcessingUrl("/sign-in")
        .defaultSuccessUrl("/home/index", true)
        .permitAll()
        .and()
        .logout()
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .clearAuthentication(true)
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/login?logout")
        .and()
        .exceptionHandling()
        .accessDeniedPage("/403")
        .and()
        .csrf().disable()
        .authenticationManager(manager)
        .httpBasic()
        .and()
        .oauth2Login()
        .loginPage("/login")
        .defaultSuccessUrl("/home/index", true)
        .userInfoEndpoint()
        .userService(customOAuth2UserDetailService)
        .and()
        .successHandler(customOAuth2SuccessHandler)
        .failureHandler(customOAuth2FailtureHandler)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		;
		
		return http.build();
	}
	
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) ->
                web.ignoring()
                        .requestMatchers("/js/**", "/css/**");
    }
}