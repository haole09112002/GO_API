package com.GOBookingAPI.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GOBookingAPI.entities.Message;
@Repository
public interface MessageRepository  extends JpaRepository<Message, Integer>{
	@Query(value = "SELECT m FROM Message m  WHERE m.conversation = :conversation")
	List<Message> getAllByConversationId(@Param("conversation") int conversation);
}
