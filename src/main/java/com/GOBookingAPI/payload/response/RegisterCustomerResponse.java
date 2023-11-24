package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class RegisterCustomerResponse extends RegisterResponse{

    public RegisterCustomerResponse() {

    }

    public RegisterCustomerResponse(int id, String fullName, String email, boolean isNonBlock, String phoneNumber, Date dateOfBirth, boolean isMale, String avtUrl) {
        super(id, fullName, email, isNonBlock, phoneNumber, dateOfBirth, isMale, avtUrl);
    }
}
