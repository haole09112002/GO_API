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

    protected int id ;

    protected int senderId;

    protected int receiverId;

    protected String content;

    protected long createAt;
}
