package com.GOBookingAPI.services;

import java.util.Date;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.ChangeCustomerInfoRequest;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.utils.DriverStatus;

public interface CustomerService {

    CustomerResponse getById(int id);


    CustomerBaseInfoResponse getBaseInfoById(int id);

    PagedResponse<CustomersResponse> getCustomerPageAndSort(Date from, Date to, Boolean isNonBlock,  String searchField,
    														String keyword, String sortType, String sortField, int size, int page);


    CustomerResponse changeInfo(int id, String email, ChangeCustomerInfoRequest req);
}
