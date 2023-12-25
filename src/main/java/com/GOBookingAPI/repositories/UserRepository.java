package com.GOBookingAPI.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByEmail(String email);
	
	@Modifying
	@Transactional
	@Query(value = "Update railway.user as u set u.is_non_block = ?1 where u.id = ?2" , nativeQuery = true)
	void UpdateIsNonBlock(boolean isnonblock , int id);
}
