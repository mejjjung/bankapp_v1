package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

public class CustomPageException extends RuntimeException {
	
	private HttpStatus Status;
	
	public CustomPageException(String message, HttpStatus Status) {
		super(message);
		this.Status = Status;
	}
}
