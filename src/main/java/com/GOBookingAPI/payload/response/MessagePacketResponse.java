package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.payload.request.CreateMessageRequest;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagePacketResponse extends CreateMessageRequest {

    private long createAt;

    @Override
    public String toString() {
        return "MessagePacketResponse{" +
                "senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", content='" + content + '\'' +
                ", conversationId=" + conversationId +
                ", time=" + createAt +
                '}';
    }
}
