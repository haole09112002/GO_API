package com.GOBookingAPI.repositories;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
