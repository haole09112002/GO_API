package com.GOBookingAPI.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/home")
public class HomeController {
	 @GetMapping("/index")
	    public String hello(Model model) {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        model.addAttribute("authentication", authentication);
	        String token = authentication.getPrincipal().toString();
	        log.info(token);
	        return "home";
	    }
}
