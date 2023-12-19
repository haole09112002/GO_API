package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingResponse {

    private int id;

    private long createAt;

    private String pickUpLocation;

    private String pickUpAddress;

    private String dropOffAddress;

    private String dropOffLocation;

    private BookingStatus status;

    private double amount;

    private Long predictTime;

    private Long startTime;

    private Long endTime;

    private double distance;

    private com.GOBookingAPI.enums.VehicleType vehicleType;

    @JsonProperty("payment")
    PaymentResponse paymentResponse;

    @JsonProperty("driver")
    DriverBaseResponse driver;

    @JsonProperty("customer")
    CustomerBaseInfoResponse customer;

    @JsonProperty("review")
    ReviewResponse review;
}
