package com.GOBookingAPI.exceptions;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean success;
	private String error;
	private HttpStatus  status;
	private String messages;
	private Instant timestamp;
	
	public ErrorResponse(Boolean success,String messages, String error, HttpStatus  status) {
		this.messages = messages;
		this.success = success;
		this.error = error;
		this.status = status;
		this.timestamp = Instant.now();
	}
}
