package com.pch777.bargains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Resource not found")
public class ResourceNotFoundException extends Exception {
	 
	private static final long serialVersionUID = -3726027955748747761L;

	public ResourceNotFoundException() {
    }
 
    public ResourceNotFoundException(String msg) {
        super(msg);
    }   
    
}
