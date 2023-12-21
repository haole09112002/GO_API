package com.GOBookingAPI.mapper;

import com.GOBookingAPI.entities.*;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.payload.response.*;

public class BookingMapper {

    public static BookingResponse bookingToBookingResponse(Booking booking, User user) {
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());

        Driver driver = booking.getDriver();
        if (driver != null) {
            DriverBaseResponse driverBaseResp = new DriverBaseResponse();
            driverBaseResp.setGender(driver.isGender());
            driverBaseResp.setFullName(driver.getFullName());
            driverBaseResp.setLicensePlate(driver.getLicensePlate());
            driverBaseResp.setPhoneNumber(driver.getUser().getPhoneNumber());
            driverBaseResp.setRating(driver.getRating());
            driverBaseResp.setId(driver.getId());
            driverBaseResp.setAvatarUrl(driver.getUser().getAvatarUrl());
            resp.setDriver(driverBaseResp);
        }

        Payment payment = booking.getPayment();
        if (payment != null) {
            PaymentResponse paymentResponse = new PaymentResponse(payment);
            resp.setPaymentResponse(paymentResponse);
        }

        Customer customer = booking.getCustomer();
        resp.setCustomer(new CustomerBaseInfoResponse(customer));

        Review review = booking.getReview();
        if (review != null)
            resp.setReview(new ReviewResponse(review));

        if (user != null && user.getFirstRole().getName().equals(RoleEnum.DRIVER) && (booking.getStatus() == BookingStatus.REFUNDED || booking.getStatus() == BookingStatus.WAITING_REFUND)) {
            resp.setStatus(BookingStatus.CANCELLED);
        } else
            resp.setStatus(booking.getStatus());

        resp.setStartTime(booking.getStartTime() != null ? booking.getStartTime().getTime() : null);
        resp.setEndTime(booking.getEndTime() != null ? booking.getEndTime().getTime() : null);
        resp.setCreateAt(booking.getCreateAt().getTime());
        resp.setAmount(booking.getAmount());
        resp.setDropOffLocation(booking.getDropOffLocation());
        resp.setPickUpLocation(booking.getPickUpLocation());
        resp.setDropOffAddress(booking.getDropOffAddress());
        resp.setPickUpAddress(booking.getPickUpAddress());
        resp.setVehicleType(booking.getVehicleType());
        return resp;
    }
}
