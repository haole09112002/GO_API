package com.GOBookingAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.VehicleType;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleType, Integer>{
	VehicleType findByName(String name);
}
