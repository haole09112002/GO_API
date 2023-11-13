package com.GOBookingAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GOBookingAPI.entities.Conservation;

public interface ConservationRepository extends JpaRepository<Conservation, Integer>{

}
