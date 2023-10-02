package com.GOBookingAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GOBookingAPI.entities.TestEntity;

public interface TestRepository extends JpaRepository<TestEntity, Integer> {
	
}
