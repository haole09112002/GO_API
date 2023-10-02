package com.GOBookingAPI.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.GOBookingAPI.entities.TestEntity;
import com.GOBookingAPI.services.TestService;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private TestService testService;
	
	@GetMapping("/list")
	public List<TestEntity> getAll(){
		return testService.getAllTestData();
	};
}
