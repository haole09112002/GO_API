package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private int id;

    private String email;

    private String phoneNumber;

    private Date createDate ;

    private Boolean isNonBlock;

    private String avatarUrl;

    private RoleEnum role;
}
