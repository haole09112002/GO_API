package com.GOBookingAPI.repositories;

import java.util.Date;
import java.util.List;

import com.GOBookingAPI.payload.dto.BookingStatistic;
import jakarta.persistence.NamedNativeQuery;
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
			+ " from railway.driver as d inner join railway.user as u on u.id = d.user_id where u.id =?1" , nativeQuery = true)
	UserDriverProjection getStatusAndIsNonBlock(int id);
	
	@Modifying
	@Transactional
	@Query(value  = "Update railway.driver as d set d.status = 'OFF' where d.id in ?1" , nativeQuery = true) 
	void activeDriver(List<Integer>  ids);
	
	@Modifying
	@Transactional
	@Query(value  = "Update railway.driver as d set d.status = 'REFUSED' where d.id in ?1" , nativeQuery = true) 
	void refuseDriver(List<Integer>  ids);
	
	@Modifying
	@Transactional
	@Query(value  = "Update railway.driver as d set d.status = 'BLOCK' where d.id = ?1" , nativeQuery = true) 
	void blockDriver(int  id); 
	
	@Modifying
	@Transactional
	@Query(value  = "Update railway.driver as d set d.status = 'OFF' where d.id = ?1" , nativeQuery = true) 
	void nonBlockDriver(int  id);

	@Query(value = "SELECT " +
			"    COUNT(*) AS total, " +
			"    COALESCE(SUM(CASE WHEN booking.status = 'COMPLETE' THEN 1 END), 0) AS completeCount, " +
			"    COALESCE(SUM(CASE WHEN booking.status = 'COMPLETE' THEN booking.amount END), 0) AS totalAmount," +
			"    COALESCE(SUM(CASE WHEN ((booking.status = 'CANCELLED' OR booking.status = 'WAITING_REFUND' OR booking.status = 'REFUNDED') AND booking.reason_type = 'DRIVER') THEN 1 END), 0) AS quantityCancelByDriver, " +
			"    COALESCE(SUM(CASE WHEN review.rating = '5' THEN 1 END), 0) AS rating5Count," +
			"    COALESCE(SUM(CASE WHEN review.rating = '4' THEN 1 END), 0) AS rating4Count," +
			"    COALESCE(SUM(CASE WHEN review.rating = '3' THEN 1 END), 0) AS rating3Count," +
			"    COALESCE(SUM(CASE WHEN review.rating = '2' THEN 1 END), 0) AS rating2Count," +
			"    COALESCE(SUM(CASE WHEN review.rating = '1' THEN 1 END), 0) AS rating1Count," +
			"    COALESCE(SUM(CASE WHEN review.rating = '0' THEN 1 END), 0) AS rating0Count" +
			" FROM booking" +
			" LEFT JOIN driver ON booking.driver_id = driver.id" +
			" LEFT JOIN review ON booking.id = review.booking_id" +
			" WHERE driver.id = :id AND DATE(booking.create_at) BETWEEN :from AND :to ", nativeQuery = true)
	BookingStatistic statisticalBooking(@Param(value = "from") Date from, @Param(value = "to") Date to, @Param(value = "id") int id);
}
