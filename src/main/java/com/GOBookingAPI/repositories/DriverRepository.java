package com.GOBookingAPI.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
//	@Query("SELECT d FROM DRIVER d WHERE d.status = :status")
//	List<Driver> findDriverStatus(@Param("status") String status );
}
