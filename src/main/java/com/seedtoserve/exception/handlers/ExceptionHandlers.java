package com.seedtoserve.exception.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleException(MethodArgumentNotValidException ex){
		
		// Get first validation error
		FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
		
		// Extract custom message 
		String errorMessage = fieldError.getDefaultMessage();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorMessage);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntime(RuntimeException ex) {
	    return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
