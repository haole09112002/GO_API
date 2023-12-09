package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.payload.request.CreateMessageRequest;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagePacketResponse extends CreateMessageRequest {

    private Date time;

    @Override
    public String toString() {
        return "MessagePacketResponse{" +
                "id_sender=" + id_sender +
                ", id_receiver=" + id_receiver +
                ", content='" + content + '\'' +
                ", id_conversation=" + id_conversation +
                ", time=" + time +
                '}';
    }
}
