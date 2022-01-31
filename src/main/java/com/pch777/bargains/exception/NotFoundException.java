package com.pch777.bargains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Resource not found")
public class NotFoundException extends Exception {
	
	private static final long serialVersionUID = 9063190116094339341L;

	public NotFoundException() {
    }
 
    public NotFoundException(String msg) {
        super(msg);
    }   
    
}
