package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.utils.DriverStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DriverStatusResponse {
    private int driverId;
    private DriverStatus status;
}
