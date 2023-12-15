package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerBaseInfoResponse {
    private int id;
    private String fullName;
    private Boolean gender;
    private String phoneNumber;
    private String avatarUrl;
}
