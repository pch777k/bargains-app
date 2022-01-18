package com.pch777.bargains.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.bargains.service.InitializerService;

@RestController
@RequestMapping("/api")
public class DataController {
	
	private InitializerService initializerService;	
	private final int NUMBER_OF_VOTES;
	
	public DataController(InitializerService initializerService, 
			@Value("${bargainapp.number-of-votes}") int nUMBER_OF_VOTES) {
		this.initializerService = initializerService;
		this.NUMBER_OF_VOTES = nUMBER_OF_VOTES;
	}

	@PostMapping("/init-data")
	@Transactional
	public ResponseEntity<String> initializeData() throws Exception {
		initializerService.initUserData();
		initializerService.initBargainData();
		initializerService.initCommentData();
		initializerService.initVoteData(NUMBER_OF_VOTES);
		return ResponseEntity.ok("successful data initialization");
	}

}
