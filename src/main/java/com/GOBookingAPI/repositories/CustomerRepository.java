package com.GOBookingAPI.repositories;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.payload.response.CustomerDetailResponse;
import com.GOBookingAPI.repositories.projection.CustomerDetailProjection;
import com.GOBookingAPI.repositories.projection.CustomerProjection;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
	@Query(value = "select u.id , u.email, c.full_name as fullname, u.phone_number as phonenumber ,u.is_non_block as isnonblock  from railway.user as u inner join gobooking.customer as c on u.id = c.user_id"
			, nativeQuery = true)
	Page<CustomerProjection> getCustomerPageAndSort(Pageable pageable);
	
	@Query(value = "select u.id, u.create_date ,u.email,u.is_non_block,u.phone_number, c.date_of_birth,c.full_name , c.gender from railway.user as u inner join gobooking.customer as c on u.id = c.user_id where u.id= ?1"
			, nativeQuery = true)
	CustomerDetailProjection findByIdByAdmin(int id);
}
