package com.GOBookingAPI.controller;

import com.GOBookingAPI.entities.Conversation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversation ")
public class ConversationController {

    public Conversation getConversation(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return null;

    }
	
}
