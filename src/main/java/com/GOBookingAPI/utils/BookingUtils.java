package com.GOBookingAPI.utils;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.User;

public class BookingUtils {
    public static boolean bookingBelongToUser(Booking booking, User user){
        return user.getId() == (booking.getCustomer().getUser().getId());
    }
}
