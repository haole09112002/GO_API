package com.GOBookingAPI.repositories;

import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentDayProjection;
import com.GOBookingAPI.repositories.projection.StatisticsPaymentMonthProjection;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	@Query(value = "SELECT DATE(p.time_stamp) as date, SUM(p.amount) as amount, count(p.time_stamp) as total "
			+ "FROM payment as p "
			+ "WHERE p.time_stamp between :from and :to "
			+ "GROUP BY date "
			+ "ORDER BY date asc", nativeQuery = true)
	List<StatisticsPaymentDayProjection> getStatisticsDate(@Param("from") Date from, @Param("to") Date to);
	
	
	@Query(value = "SELECT DATE(p.time_stamp) as date, SUM(p.amount) as amount, count(p.time_stamp) as total "
			+ "FROM payment as p "
			+ "WHERE Month(p.time_stamp) = :month and Year(p.time_stamp) = :year "
			+ "GROUP BY date "
			+ "ORDER BY date asc", nativeQuery = true)
	List<StatisticsPaymentDayProjection> getStatisticsDateofMonth(@Param("month") int month, @Param("year") int year);
	
	
	@Query(value = "SELECT MONTH(p.time_stamp) as month, SUM(p.amount) as amount , count(p.time_stamp) as total "
			+ "FROM payment as p "
			+ "WHERE Year(p.time_stamp) = :year "
			+ "GROUP BY month "
			+ "ORDER BY month asc" , nativeQuery = true)
	List<StatisticsPaymentMonthProjection> getStatisticsMonthOfYear( @Param("year") int year);
	
	

}
