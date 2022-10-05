package com.pch777.bargains.web;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.bargains.service.InitializerService;
import com.pch777.bargains.utility.ValuesProperties;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Data", description = "Initializing sample data")
public class DataController {
	
	private final InitializerService initializerService;	
	private final ValuesProperties valuesProperties;

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
		initializerService.initVoteData(valuesProperties.getNumberOfVotes());
		return ResponseEntity.ok("successful data initialization");
		}
	catch (Exception e) {
	      return ResponseEntity.internalServerError().build();
	    }
	
	}
}
