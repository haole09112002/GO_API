package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Conversation;
import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.payload.request.CreateConservationRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.ConservationRepository;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.IConservationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ConservationServiceImpl implements IConservationService {

    @Autowired
    private ConservationRepository conservationRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    @Transactional
    public void createConservation(Booking booking) {
        Conversation conservation = new Conversation();
        conservation.setDriver(booking.getDriver());
        conservation.setCustomer(booking.getCustomer());
        conservation.setBooking(booking);
        conservation.setCreateAt(new Date());
        conservationRepository.save(conservation);
    }

}
