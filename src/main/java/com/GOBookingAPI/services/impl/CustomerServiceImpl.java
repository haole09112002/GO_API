package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.GOBookingAPI.payload.response.CustomerDetailResponse;
import com.GOBookingAPI.payload.response.CustomersResponse;
import com.GOBookingAPI.payload.response.PagedResponse;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.projection.CustomerDetailProjection;
import com.GOBookingAPI.repositories.projection.CustomerProjection;
import com.GOBookingAPI.services.ICustomerService;
import org.modelmapper.ModelMapper;
@Service
public class CustomerServiceImpl implements ICustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public PagedResponse<CustomersResponse> getCustomerPageAndSort(int offset, int pagesize, String field) {
		Page<CustomerProjection> customerPage = customerRepository.getCustomerPageAndSort(PageRequest.of(offset, pagesize).withSort(Sort.by(field).ascending()));
		
		
		List<CustomersResponse> list =  customerPage.getContent().stream()
				.map(customer -> new CustomersResponse(
						customer.getId(), 
						customer.getEmail(), 
						customer.getFullname(),
						customer.getPhonenumber(),
						customer.getIsnonblock()))
				.collect(Collectors.toList());
		
		return new PagedResponse<CustomersResponse>(list, customerPage.getNumber(), customerPage.getSize(),
				customerPage.getTotalElements(), customerPage.getTotalPages(), customerPage.isLast());
	}

	@Override
	public CustomerDetailResponse getCustomerDetailById(int id) {
		ModelMapper modelMap = new ModelMapper();
		CustomerDetailProjection customerDetailProjection = customerRepository.findByIdByAdmin(id);
		modelMap.map(customerDetailProjection, CustomerDetailProjection.class);
		return new CustomerDetailResponse(
				customerDetailProjection.getId(),
				customerDetailProjection.getEmail(),
				customerDetailProjection.getFull_name(),
				customerDetailProjection.getPhone_number(),
				customerDetailProjection.getIs_non_block(),
				customerDetailProjection.getCreate_date() ,
				customerDetailProjection.getDate_of_birth(),
				customerDetailProjection.getGender());
	}

	
	
}
