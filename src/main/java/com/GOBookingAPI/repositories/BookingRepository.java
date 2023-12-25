package com.GOBookingAPI.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.repositories.projection.StatisticsBookingAmountMonthProjection;
import com.GOBookingAPI.repositories.projection.StatisticsBookingBaseProjection;
import com.GOBookingAPI.repositories.projection.StatisticsBookingCountAndSumProjections;

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
    
    
    @Query(value = "select  Date(b.create_at) as day, Count(*) as count, sum(amount) as total "
    		+ "from booking as b "
    		+ "where b.create_at between :from and :to "
    		+ "group by day" , nativeQuery =  true)
    List<StatisticsBookingCountAndSumProjections> getCountAndSumDay(@Param("from") Date from, @Param("to") Date to);
    
    @Query(value = "select  Date(b.create_at) as day,b.amount, b.drop_off_location as dropOff, b.pick_up_location as pickUp, b.status,b.vehicle_type as vehicle  "
    		+ "from booking as b "
    		+ "where b.create_at between :from and :to and b.status in('complete','cancelled', 'refunded')"
    		, nativeQuery =  true)
    List<StatisticsBookingBaseProjection> getBaseBooking(@Param("from") Date from, @Param("to") Date to);
    
    @Query(value = "select  Month(b.create_at) as day, Count(*) as count, sum(amount) as total "
    		+ "from booking as b "
    		+ "where Month(b.create_at) between :fromMonth and :toMonth and year(b.create_at) between :fromYear and :toYear "
    		+ "group by day" , nativeQuery =  true)
    List<StatisticsBookingCountAndSumProjections> getCountAndSumMonth(@Param("fromMonth") int fromMonth, @Param("toMonth") int toMonth,
    																  @Param("fromYear") int fromYear,@Param("toYear") int toYear);
    
    
    @Query(value = "select Month(b.create_at) as month, sum(b.amount)as amount , Count(b.status) as count "
    		+ "from booking as b "
    		+ "where Month(b.create_at)between :fromMonth and :toMonth and b.status = :status  and year(b.create_at) between :fromYear and :toYear  "
    		+ "group by month "
    		+ "order by month desc" , nativeQuery = true)
    List<StatisticsBookingAmountMonthProjection> getAmuontWithStatus(@Param("fromMonth") int fromMonth, 
    																@Param("toMonth") int toMonth,
    																@Param("status") String status,
    																@Param("fromYear") int fromYear,
    																@Param("toYear") int toYear);
}
