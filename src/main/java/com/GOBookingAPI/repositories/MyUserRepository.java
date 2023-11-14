package com.GOBookingAPI.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.User;

@Repository
public interface MyUserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByEmail(String email);
}
