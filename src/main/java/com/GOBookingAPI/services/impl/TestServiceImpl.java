package com.GOBookingAPI.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.repositories.TestRepository;
import com.GOBookingAPI.services.TestService;
import  com.GOBookingAPI.entities.TestEntity;

@Service
public class TestServiceImpl implements TestService{
	@Autowired
	private TestRepository testRepository;
	
	@Override
	public List<TestEntity> getAllTestData() {
		
		return testRepository.findAll();
	}

}
