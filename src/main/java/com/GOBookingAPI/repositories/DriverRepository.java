package com.GOBookingAPI.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.utils.DriverStatus;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

	@Query("SELECT d FROM Driver d WHERE status = :status")
	List<Driver> findDriverStatus(@Param("status") DriverStatus status );

}
