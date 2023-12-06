package com.GOBookingAPI.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {




}
