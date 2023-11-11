package com.GOBookingAPI.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	private Boolean success;
	private String error;
	private HttpStatus  status;
	private List<String> messages;
	private Instant timestamp;

	public ErrorResponse(Boolean success,List<String> messages, String error, HttpStatus  status) {
		setMessages(messages);
		this.success = success;
		this.error = error;
		this.status = status;
		this.timestamp = Instant.now();
	}

	public List<String> getMessages() {

		return messages == null ? null : new ArrayList<>(messages);
	}

	public final void setMessages(List<String> messages) {

		if (messages == null) {
			this.messages = null;
		} else {
			this.messages = Collections.unmodifiableList(messages);
		}
	}
}