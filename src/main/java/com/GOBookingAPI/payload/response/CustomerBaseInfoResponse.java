package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.entities.Customer;
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

    public CustomerBaseInfoResponse(Customer customer){
        this.id = customer.getId();
        this.fullName = customer.getFullName();
        this.gender = customer.getGender();
        this.phoneNumber = customer.getUser().getPhoneNumber();
        this.avatarUrl = customer.getUser().getAvatarUrl();
    }
}
