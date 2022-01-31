package com.pch777.bargains.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.bargains.service.InitializerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Data", description = "Initializing sample data")
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
	@Operation(summary = "Initialize sample data", responses = {
            @ApiResponse(description = "Load sample data successfully", 
            	responseCode = "200",
                content = @Content),
            @ApiResponse(description = "Something went wrong",
            	responseCode = "500",
            	content = @Content)
    })
	public ResponseEntity<String> initializeData() throws Exception {
		try {
		initializerService.initUserData();
		initializerService.initBargainData();
		initializerService.initCommentData();
		initializerService.initVoteData(NUMBER_OF_VOTES);
		return ResponseEntity.ok("successful data initialization");
		}
	catch (Exception e) {
	      return ResponseEntity.internalServerError().build();
	    }
	
	}
}
