package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.ChangeCustomerInfoRequest;
import com.GOBookingAPI.payload.response.*;

public interface CustomerService {

    CustomerResponse getById(int id);

    CustomerDetailResponse getCustomer(int id);

    CustomerBaseInfoResponse getBaseInfoById(int id);

    PagedResponse<CustomersResponse> getCustomerPageAndSort(int offset , int pagesize , String field);

    CustomerDetailResponse getCustomerDetailById(int id);

    CustomerResponse changeInfo(int id, String email, ChangeCustomerInfoRequest req);
}
