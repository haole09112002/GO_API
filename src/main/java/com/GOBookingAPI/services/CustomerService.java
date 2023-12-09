package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.payload.response.CustomerBaseInfoResponse;

public interface CustomerService {

    Customer getById(int id);

    CustomerBaseInfoResponse getBaseInfoById(int id);
}
