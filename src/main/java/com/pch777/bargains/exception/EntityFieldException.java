package com.pch777.bargains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EntityFieldException extends Exception {
	 
	private static final long serialVersionUID = -3217375734829776491L;

	private String field;
	
	public EntityFieldException() {
    }
 
    public EntityFieldException(String msg) {
        super(msg);
    } 
    
    public EntityFieldException(String msg, String field) {
        super(msg);
        this.field = field;
    }

	public String getField() {
		return field;
	} 
}
