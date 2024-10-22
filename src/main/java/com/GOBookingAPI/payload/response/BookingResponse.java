package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.entities.*;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingResponse {

    private int id;

    private Date createAt;

    private String pickUpLocation;

    private String pickUpAddress;

    private String dropOffAddress;

    private String dropOffLocation;

    private BookingStatus status;

    private double amount;

    private long predictTime;

    private Integer driverId;

    private PaymentMethod paymentMethod;

    private Integer customerId;

    private double distance;

    private com.GOBookingAPI.enums.VehicleType vehicleType;
}
