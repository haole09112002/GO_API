package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TravelInfoResponse {
    private String pickUpLocation;
    private String dropOffLocation;
    private Map<Integer, Long> amounts;
}
