package com.GOBookingAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.VehicleType;
import com.google.common.base.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleType, Integer>{
	Optional<VehicleType> findByName(String name);
}
