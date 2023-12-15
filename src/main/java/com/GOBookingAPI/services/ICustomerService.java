package com.GOBookingAPI.services;


import java.util.List;

import org.springframework.data.domain.Page;

import com.GOBookingAPI.payload.response.CustomerDetailResponse;
import com.GOBookingAPI.payload.response.CustomersResponse;
import com.GOBookingAPI.payload.response.PagedResponse;

public interface ICustomerService {
	PagedResponse<CustomersResponse> getCustomerPageAndSort(int offset , int pagesize , String field);
	
	CustomerDetailResponse getCustomerDetailById(int id);
}
