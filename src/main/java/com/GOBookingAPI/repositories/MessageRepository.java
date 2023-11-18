package com.GOBookingAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GOBookingAPI.entities.Message;

public interface MessageRepository  extends JpaRepository<Message, Integer>{

}
