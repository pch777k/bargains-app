package com.pch777.bargains.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pch777.bargains.service.InitializerService;
import com.pch777.bargains.service.MailService;

@Controller
public class InitializerController {
	
	private InitializerService initializerService;
	private MailService mailService;
	private final int NUMBER_OF_VOTES;
	
	public InitializerController(InitializerService initializerService, MailService mailService, 
			@Value("${bargainapp.number-of-votes}") int nUMBER_OF_VOTES) {
		this.initializerService = initializerService;
		this.mailService = mailService;
		this.NUMBER_OF_VOTES = nUMBER_OF_VOTES;
	}

	@RequestMapping("/init-data")
	public String initSampleData() throws Exception {
		initializerService.initUserData();
		initializerService.initBargainData();
		initializerService.initCommentData();
		initializerService.initVoteData(NUMBER_OF_VOTES);
		mailService.sendEmail("bargainsapp@gmail.com", "bargainsapp@gmail.com", "Someone loaded sample data", "Load sample data");
		return "redirect:/";
	}

}
