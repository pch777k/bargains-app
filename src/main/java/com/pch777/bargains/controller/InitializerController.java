package com.pch777.bargains.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pch777.bargains.service.InitializerService;
import com.pch777.bargains.service.MailService;
import com.pch777.bargains.utility.ValuesProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class InitializerController {
	
	private final InitializerService initializerService;
	private final  MailService mailService;
	private final ValuesProperties valuesProperties;
	


	@RequestMapping("/init-data")
	public String initSampleData() throws Exception {
		initializerService.initUserData();
		initializerService.initBargainData();
		initializerService.initCommentData();
		initializerService.initVoteData(valuesProperties.getNumberOfVotes());
		mailService.sendEmail("bargainsapp@gmail.com", "bargainsapp@gmail.com", "Someone loaded sample data", "Load sample data");
		return "redirect:/";
	}

}
