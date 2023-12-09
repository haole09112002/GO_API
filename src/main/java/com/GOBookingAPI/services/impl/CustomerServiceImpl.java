package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.response.CustomerBaseInfoResponse;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer getById(int id) {
        return customerRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay khach hang: " + id));
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
}
