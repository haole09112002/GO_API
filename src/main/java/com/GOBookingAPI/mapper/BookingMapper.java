package com.GOBookingAPI.mapper;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.payload.response.BookingResponse;

public class BookingMapper {

    public static BookingResponse bookingToBookingResponse(Booking booking){
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setDriverId(booking.getDriver() != null ? booking.getDriver().getId() : null);
        resp.setCreateAt(booking.getCreateAt());
        resp.setPaymentMethod(booking.getPayment() != null ? booking.getPayment().getPaymentMethod() : null);
        resp.setCustomerId(booking.getCustomer().getId());
        resp.setAmount(booking.getAmount());
        resp.setDropOffLocation(booking.getDropoffLocation());
        resp.setPickupLocation(booking.getPickupLocation());
        resp.setStatus(booking.getStatus());
        resp.setVehicleType(booking.getVehicleType());
        return resp;
    }
}
