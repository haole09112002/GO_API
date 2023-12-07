package com.GOBookingAPI.payload.response;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageResponse {

    private int id ;

    private int senderId;

    private int receiverId;

    private String content;

    private Date createAt;
}
