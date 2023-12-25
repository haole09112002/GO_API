package com.GOBookingAPI.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.entities.Review;
import com.GOBookingAPI.repositories.projection.StatisticsReviewBaseProjection;

import lombok.val;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{
	
	@Query(value = "SELECT "
			+ "    Date(r.create_at) as date, count(r.create_at) as count, "
			+ "    SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END) as fiveStar, "
			+ "    SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END) as fourStar, "
			+ "    SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END) as threeStar, "
			+ "    SUM(CASE WHEN rating = 2 THEN 1 ELSE 0 END) as twoStar, "
			+ "    SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) as oneStar "
			+ "FROM "
			+ "    review as r "
			+ "where r.create_at between :from and :to "
			+ "GROUP BY "
			+ "    date" ,nativeQuery =  true )
	List<StatisticsReviewBaseProjection> getStatisticsReviewDay(@Param("from") Date from, @Param("to") Date to);
	
	@Query(value = "select r.content "
			+ "from review as r "
			+ "where Date(r.create_at) = ?1  and r.rating in (3,4,5) "
			+ "order by r.rating desc limit 1 ", nativeQuery =  true)
	String getReviewPositiveDay(Date date);
	
	@Query(value = "select  r.content "
			+ "from review as r "
			+ "where Date(r.create_at) = ?1  and r.rating in (1,2) "
			+ "order by r.rating asc limit 1 ", nativeQuery =  true)
	String getReviewNegativeDay(Date date);
	
	@Query(value = "SELECT "
			+ "    Month(r.create_at) as date, count(r.create_at) as count, "
			+ "    SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END) as fiveStar, "
			+ "    SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END) as fourStar, "
			+ "    SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END) as threeStar, "
			+ "    SUM(CASE WHEN rating = 2 THEN 1 ELSE 0 END) as twoStar, "
			+ "    SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) as oneStar "
			+ "FROM "
			+ "    review as r "
			+ "where Month(r.create_at) between :fromMonth and :toMonth and year(r.create_at) between :fromYear and :toYear "
			+ "GROUP BY "
			+ "    date" ,nativeQuery =  true )
	List<StatisticsReviewBaseProjection> getStatisticsReviewMonth(@Param("fromMonth") int fromMonth, @Param("toMonth") int toMonth,
			  													  @Param("fromYear") int fromYear,@Param("toYear") int toYear);
	
	@Query(value = "select r.content "
			+ "from review as r "
			+ "where Month(r.create_at) = ?1  and r.rating in (3,4,5) "
			+ "order by r.rating desc limit 1 ", nativeQuery =  true)
	String getReviewPositiveMonth(int date);
	
	@Query(value = "select  r.content "
			+ "from review as r "
			+ "where Month(r.create_at) = ?1  and r.rating in (1,2) "
			+ "order by r.rating asc limit 1 ", nativeQuery =  true)
	String getReviewNegativeMonth(int date);
}
