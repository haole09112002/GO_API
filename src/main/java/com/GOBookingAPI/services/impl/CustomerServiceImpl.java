package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.projection.CustomerDetailProjection;
import com.GOBookingAPI.repositories.projection.CustomerProjection;
import com.GOBookingAPI.services.ICustomerService;
import org.modelmapper.ModelMapper;
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerResponse getById(int id) {
        Customer customer = customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));
   		return new CustomerResponse(customer);
    }

	@Override
	public CustomerDetailResponse getCustomer(int id) {
//	Customer customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));
		return null;
	}

	@Override
    public CustomerBaseInfoResponse getBaseInfoById(int id) {
        Customer customer = customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));
        CustomerBaseInfoResponse baseInfo = new CustomerBaseInfoResponse();
        baseInfo.setId(customer.getId());
        baseInfo.setFullName(customer.getFullName());
        baseInfo.setGender(customer.getGender());
        baseInfo.setAvatarUrl(customer.getUser().getAvatarUrl());
        baseInfo.setPhoneNumber(customer.getUser().getPhoneNumber());
        return baseInfo;
    }

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
