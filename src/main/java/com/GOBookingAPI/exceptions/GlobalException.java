package com.GOBookingAPI.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.GOBookingAPI.payload.response.BaseResponse;


@RestControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<BaseResponse> handleBaseException(BaseException e){
		BaseResponse response = BaseResponse.builder()
				.message(e.getMessage())
				.build();
		return ResponseEntity.ok(response) ;
	}
}
