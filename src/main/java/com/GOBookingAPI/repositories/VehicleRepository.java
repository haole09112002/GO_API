package com.GOBookingAPI.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.VehicleType;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleType, Integer>{
	Optional<VehicleType> findByName(com.GOBookingAPI.enums.VehicleType name);
}
