package com.pch777.bargains.controller;

import org.springframework.boot.web.servlet.error.ErrorController;

//@Controller
public class AppErrorController implements ErrorController {
	
//	@RequestMapping("/error")
//	public String handleError(HttpServletRequest request, Model model) {
//	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//	    String errorPage = "error";
//	    String pageTitle = "Error";
//	    String errorTitle = "Oops, Error";
//	    String errorText = "Oops, something went wrong.";
//	    
//	    if (status != null) {
//	        Integer statusCode = Integer.valueOf(status.toString());
//	    
//	        if(statusCode == HttpStatus.FORBIDDEN.value()) {
//	            errorPage = "error/403";
//	            pageTitle = "Error 403 Forbidden";
//	            errorTitle = "403 Forbidden";
//	            errorText = "You don't have permission to do it.";
//	        } else if(statusCode == HttpStatus.NOT_FOUND.value()) {
//	        	errorPage = "error/404";
//	        	pageTitle = "Error 404 Not Found";
//	        	errorTitle = "404 Not Found";
//	        	errorText = "Oops. Looks like this page doesn't exist.";
//	        }
//	        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
//	        	errorPage = "error/500";
//	        	pageTitle = "Error 500 Internal Server Error";
//	        	errorTitle = "500 Internal Server Error";
//	        } 
//	    }
//	    
//	    model.addAttribute("pageTitle", pageTitle);
//	    model.addAttribute("errorTitle", errorTitle);
//	    model.addAttribute("errorText", errorText);
//	    
//	    return errorPage;
//	}

}
