package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class DriverBaseInfoResponse extends RegisterResponse{
    protected String licensePlate;
    protected Double rating ;
    protected VehicleType vehicleType;
}
