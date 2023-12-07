package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationResponse {

    private int id;

    private int bookingId;

    private List<MessageResponse> messageResponses;
}
