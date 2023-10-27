package com.GOBookingAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{
	Review findById(int id);
}
