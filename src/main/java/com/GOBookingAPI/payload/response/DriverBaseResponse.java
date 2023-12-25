package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DriverBaseResponse {
    private int id;
    private String fullName;
    private boolean gender;
    private String phoneNumber;
    private String licensePlate;
    private Double rating ;
    private String avatarUrl ;
}
