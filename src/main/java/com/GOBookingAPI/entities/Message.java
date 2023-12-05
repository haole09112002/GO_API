package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.Date;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "Message")
public class Message implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id ;
	@Column
	private int senderId;
	@Column
	private int receiverId;
	@Column
	private String content;
	@Column
	private Date createAt;
	@Transient 
	private String to;
	
	
	@ManyToOne
	@JoinColumn(name = "conversation_id")
	private Conversation conversation;
	
	
}
