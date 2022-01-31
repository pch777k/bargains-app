package com.pch777.bargains.exception;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class CustomGlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    List<String> errors = ex
	        .getBindingResult()
	        .getFieldErrors()
	        .stream()
	        .map(x -> x.getField() + " - " + x.getDefaultMessage())
	        .collect(Collectors.toList());
	    body.put("errors", errors);
	    return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    List<String> errors = ex.getConstraintViolations()
	    		.stream()
	    		.map(x -> x.getMessage())
	    		.collect(Collectors.toList());
	    body.put("errors", errors);
	    return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(EntityFieldException.class)
	public ResponseEntity<Object> handleEntityFieldException(EntityFieldException ex) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    body.put("errors", ex.getField() + " - " + ex.getMessage());
	    return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> handleEntityFieldException(AccessDeniedException ex) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.FORBIDDEN;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    body.put("errors", "Access Denied");
	    return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    body.put("errors", "Only [DOWNVOTE, UPVOTE] values are acceptable for the VoteType field");
	    return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, HttpServletRequest req) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.NOT_FOUND;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    body.put("error", status.name());
	    body.put("message", ex.getMessage());
	    body.put("path", req.getServletPath());
	    
	    return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, HttpServletRequest req) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    body.put("error", status.name());
	    body.put("message", ex.getMessage());
	    body.put("path", req.getServletPath());
	    
	    return new ResponseEntity<>(body, status);
	}

}
