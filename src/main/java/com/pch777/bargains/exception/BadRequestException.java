package com.pch777.bargains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends Exception {

	private static final long serialVersionUID = 3472815754919514973L;

	public BadRequestException() {
    }
 
    public BadRequestException(String msg) {
        super(msg);
    }   
    
}
