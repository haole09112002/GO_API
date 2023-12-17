package com.GOBookingAPI.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.repositories.projection.UserDriverProjection;
import com.GOBookingAPI.utils.DriverStatus;


@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

	@Query("SELECT d FROM Driver d WHERE status = :status")
	List<Driver> findDriverStatus(@Param("status") DriverStatus status );

	@Query(value = "select d.status,u.is_non_block as isnonblock\r\n"
			+ " from gobooking.driver as d inner join gobooking.user as u on u.id = d.user_id where u.id =?1" , nativeQuery = true)
	UserDriverProjection getStatusAndIsNonBlock(int id);
	
	@Modifying
	@Transactional
	@Query(value  = "Update gobooking.driver as d set d.status = 'OFF' where d.id in ?1" , nativeQuery = true) 
	void activeDriver(List<Integer>  ids);
	
	@Modifying
	@Transactional
	@Query(value  = "Update gobooking.driver as d set d.status = 'REFUSED' where d.id in ?1" , nativeQuery = true) 
	void refuseDriver(List<Integer>  ids);
}
