package com.GOBookingAPI.repositories;

import com.GOBookingAPI.payload.response.ConversationResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import com.GOBookingAPI.entities.Conversation;

public interface ConservationRepository extends JpaRepository<Conversation, Integer>{

}
