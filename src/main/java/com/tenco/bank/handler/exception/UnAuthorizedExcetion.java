package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UnAuthorizedExcetion extends RuntimeException{

	private HttpStatus status;
	
	public UnAuthorizedExcetion(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
