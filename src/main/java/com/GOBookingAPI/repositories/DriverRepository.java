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
import com.GOBookingAPI.repositories.projection.DriverProjection;
import com.GOBookingAPI.utils.DriverStatus;


@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

	@Query("SELECT d FROM Driver d WHERE status = :status")
	List<Driver> findDriverStatus(@Param("status") DriverStatus status );

	@Query(value = "select d.id , d.activity_area as area , d.full_name as fullname , d.status, u.phone_number as phonenumber, u.is_non_block as isnonblock\r\n"
			+ " from railway.driver as d inner join railway.user as u on u.id = d.user_id" , nativeQuery = true)
	Page<DriverProjection> getDriverPageAndSort(Pageable pageable);
	
	@Modifying
	@Transactional
	@Query(value  = "Update railway.driver as d set d.status = 'OFF' where d.id in ?1" , nativeQuery = true) 
	void ActiveDriver(List<Integer>  ids);
}
