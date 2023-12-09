package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.payload.request.CreateMessageRequest;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MessagePacketResponse extends CreateMessageRequest {

    private Date time;
}
