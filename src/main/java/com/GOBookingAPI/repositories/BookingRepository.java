package com.GOBookingAPI.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.GOBookingAPI.repositories.projection.StatisticsBookingCountProjections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findById(int id);

    @Query("SELECT b FROM Booking b WHERE b.createAt BETWEEN :from AND :to AND b.customer.id = :customerId")
    Page<Booking> findBookingBetweenAndCustomer(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("customerId") int customerId,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b WHERE b.createAt BETWEEN :from AND :to AND b.driver.id = :driverId")
    Page<Booking> findBookingBetweenAndDriver(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("driverId") int customerId,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b WHERE b.createAt BETWEEN :from AND :to")
    Page<Booking> findBookingBetween(
            @Param("from") Date from,
            @Param("to") Date to,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b WHERE b.customer.id = :cusId AND b.driver.id = :driverId")
    List<Booking> findByCustomerId(@Param("cusId") int cusId, @Param("driverId") int driverId);

    @Query(value = "SELECT * FROM booking b " +
            " WHERE" +
            "    (:role = 'DRIVER' AND b.driver_id = :uid AND (b.status = 'PAID' OR b.status = 'FOUND' OR b.status = 'ON_RIDE')) OR " +
            "    (:role = 'CUSTOMER' AND b.customer_id = :uid AND (b.status = 'PAID' OR b.status = 'FOUND' OR b.status = 'ON_RIDE'))" +
            " ORDER BY b.create_at DESC" +
            " LIMIT 1;", nativeQuery = true)
    Optional<Booking> getCurrentActiveBooking(@Param("uid") int uid, @Param("role") String role);
    
    
    @Query(value = "select  Date(b.create_at) as day, Count(*) as count "
    		+ "from booking as b  "
    		+ "where Date(b.create_at) between :from and :to  "
    		+ " and b.status like %:status% "
    		+ "group by day "
    		+ "ORDER BY day asc" , nativeQuery = true)
    List<StatisticsBookingCountProjections> getCountByStatusDate(@Param("from") Date from, @Param("to") Date to, @Param("status") String status);
    
    @Query(value = "select  Date(b.create_at) as day, Count(*) as count "
    		+ "from booking as b  "
    		+ "where Month(b.create_at) = :month and Year(b.create_at) = :year "
    		+ " and b.status like %:status% "
    		+ "group by day " 
    		+ "ORDER BY day asc" , nativeQuery =  true)
    List<StatisticsBookingCountProjections> getCountByStatusDateOfMonth(@Param("month") int month, @Param("year") int year, @Param("status") String status);
    
    @Query(value = "select  Month(b.create_at) as day, Count(*) as count "
    		+ "from booking as b "
    		+ "where  Year(b.create_at) = :year "
    		+ " and b.status like %:status% "
    		+ "group by day "
    		+ "ORDER BY day asc " , nativeQuery =  true)
    List<StatisticsBookingCountProjections> getCountByStatusMonthOfYear(@Param("year") int year, @Param("status") String status);
}
