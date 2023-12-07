package com.GOBookingAPI.services;


import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.payload.request.CreateConservationRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.ConversationResponse;

public interface ConversationService {
    void createConservation(Booking booking);

    ConversationResponse getCurrentConversation(String email);
}
