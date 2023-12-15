package com.GOBookingAPI.controller;

import org.apache.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.GOBookingAPI.exceptions.AccessDeniedException;

import net.minidev.json.JSONObject;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	  @ExceptionHandler({ AccessDeniedException.class })
	    public ResponseEntity<Object> handleAccessDeniedException(
	      Exception ex, WebRequest request) {
		  JSONObject json = new JSONObject();
		  json.put("message", "Ban bị block !");
	        return new ResponseEntity<Object>(
	          json, new HttpHeaders(), HttpStatus.SC_FORBIDDEN);
	    }
}
