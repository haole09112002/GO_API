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
public class CustomerResponse {

    private int id;

    private String fullName ;

    private Boolean gender ;

    private long dateOfBirth;

    private String email;

    private String avatarUrl;

    private boolean isNonBlock;

    private long createAt;

    public CustomerResponse(Customer cus){
        this.id = cus.getId();
        this.fullName = cus.getFullName();
        this.gender = cus.getGender();
        this.dateOfBirth = cus.getDateOfBirth().getTime();
        this.email = cus.getUser().getEmail();
        this.avatarUrl = cus.getUser().getAvatarUrl();
        this.isNonBlock = cus.getUser().getIsNonBlock();
        this.createAt = cus.getUser().getCreateDate().getTime();
    }
}