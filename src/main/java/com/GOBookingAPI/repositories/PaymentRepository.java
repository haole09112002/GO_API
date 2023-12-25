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

	@Query(value = "SELECT DATE(p.time_stamp) as date, SUM(p.amount) as total "
			+ "FROM payment as p "
			+ "WHERE p.time_stamp between :from and :to "
			+ "GROUP BY date "
			+ "ORDER BY total desc", nativeQuery = true)
	List<StatisticsPaymentDayProjection> getStatisticsDay(@Param("from") Date from, @Param("to") Date to);
	
	
	@Query(value = "Select * from payment as p join ("
			+ "Select Date(time_stamp) as max_day, Max(amount) as max_amount from payment "
			+ " where time_stamp between :from and :to group by max_day ) as inforMax "
			+ " on Date(p.time_stamp) = inforMax.max_day and p.amount = inforMax.max_amount "
			+ "where p.time_stamp between :from and :to" , nativeQuery = true)
	List<Payment> getInforPaymentMaxDay(@Param("from") Date from, @Param("to") Date to);
	
	@Query(value = "select count(*) from payment as p  where p.time_stamp between :from and :to" , nativeQuery =  true)
	int getCountTransaction(@Param("from") Date from, @Param("to") Date to);
	
	
	@Query(value = "SELECT MONTH(p.time_stamp) as month, SUM(p.amount) as total "
			+ "FROM payment as p "
			+ "WHERE MONTH(p.time_stamp) between :from and :to and year(p.time_stamp) >= :yearFrom and year(p.time_stamp) <= :yearTo "
			+ "GROUP BY month "
			+ "ORDER BY total desc", nativeQuery = true)
	List<StatisticsPaymentMonthProjection> getStatisticsMonth(@Param("from") int from, @Param("to") int to, @Param("yearFrom") int yearFrom , @Param("yearTo") int yearTo);
	
	
	@Query(value = "WITH RankedPayment as ("
			+ "SELECT DATE(p.time_stamp) as date, "
			+ "SUM(p.amount) as total, "
			+ "ROW_NUMBER() OVER (PARTITION BY MONTH(p.time_stamp) ORDER BY SUM(p.amount) DESC) as rnk "
			+ "FROM payment as p "
			+ "WHERE month(p.time_stamp) between :from and :to "
			+ "GROUP BY month(p.time_stamp) ,date "
			+ ")"
			+ ""
			+ "SELECT date, total "
			+ "FROM RankedPayment "
			+ "WHERE rnk = 1 and year(date) >= :yearFrom and year(date) <= :yearTo ", nativeQuery =  true)
	List<StatisticsPaymentDayProjection> getInforMaxDayOfMonth(@Param("from") int from, @Param("to") int to , @Param("yearFrom") int yearFrom , @Param("yearTo") int yearTo);

	@Query(value = "select count(*) from payment as p  where month(p.time_stamp) between :from and :to" , nativeQuery =  true)
	int getCountTransaction(@Param("from") int from, @Param("to") int to);

}
