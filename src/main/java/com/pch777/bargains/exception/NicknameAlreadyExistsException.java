package com.pch777.bargains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason="Nickname already exists")
public class NicknameAlreadyExistsException extends Exception {
	 
	private static final long serialVersionUID = 1151243203624607716L;

	public NicknameAlreadyExistsException() {
    }
 
    public NicknameAlreadyExistsException(String msg) {
        super(msg);
    }    
}
