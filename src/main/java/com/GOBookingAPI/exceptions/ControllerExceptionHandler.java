package com.GOBookingAPI.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;




@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public String handlerNotFoundException(NotFoundException ex, Model model)
	{			
		model.addAttribute("error", new ErrorResponse(false,ex.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(),HttpStatus.NOT_FOUND));
		return "error";
	}
	
	
	@ExceptionHandler(BadRequestException.class)
	public String resolveException(BadRequestException ex, Model model)
	{		
		model.addAttribute("error", new ErrorResponse(false,ex.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(),HttpStatus.BAD_REQUEST));
		return "error";
		
	}
	
	@ExceptionHandler(AppException.class)
	public String resolveException(AppException ex, Model model)
	{		
		model.addAttribute("error",new ErrorResponse(false,ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),HttpStatus.INTERNAL_SERVER_ERROR));
		return "error";
	}
	
	@ExceptionHandler(FileStorageException.class)
	public String resolveException(FileStorageException ex, Model model)
	{		
		model.addAttribute("error",new ErrorResponse(false,ex.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(),HttpStatus.NOT_FOUND));
		return "error";
	}
}