package com.GOBookingAPI.payload.request;

import com.GOBookingAPI.utils.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverStatusRequest {
    DriverStatus status;
}
