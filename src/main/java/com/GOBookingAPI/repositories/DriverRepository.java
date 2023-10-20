package com.GOBookingAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
	Driver findById(int id);
}
