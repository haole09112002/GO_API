package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.payload.response.CustomerBaseInfoResponse;
import com.GOBookingAPI.payload.response.CustomerDetailResponse;
import com.GOBookingAPI.payload.response.CustomersResponse;
import com.GOBookingAPI.payload.response.PagedResponse;

public interface CustomerService {

    Customer getById(int id);

    CustomerDetailResponse getCustomer(int id);

    CustomerBaseInfoResponse getBaseInfoById(int id);

    PagedResponse<CustomersResponse> getCustomerPageAndSort(int offset , int pagesize , String field);

    CustomerDetailResponse getCustomerDetailById(int id);
}
