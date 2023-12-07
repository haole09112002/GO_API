package com.GOBookingAPI.services.impl;

import java.util.Date;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.response.ConversationResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Conversation;
import com.GOBookingAPI.repositories.ConservationRepository;
import com.GOBookingAPI.services.ConversationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConservationRepository conservationRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public ConversationResponse getCurrentConversation(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        return null;
    }


}
