package com.pch777.bargains.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.bargains.model.Activity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Activity")
public class ActivityController {

	private ActivityService activityService;
	private UserService userService;
	
	@GetMapping(path = "/activities/{activityId}")
	@Operation(summary = "Get activity", 
				parameters = {
			@Parameter(name = "activityId", 
						description = "ID of activity", 
						required = true)},
				responses = {
            @ApiResponse(description = "Get activity successfully", 
            	responseCode = "200",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Activity.class))),
            @ApiResponse(description = "Activity not found",
            	responseCode = "404",
            	content = @Content)
    })
	public ResponseEntity<Activity> getActivityById(@PathVariable Long activityId) {
		return activityService
				.getActivityById(activityId)
				.map(activity -> ResponseEntity.ok(activity))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping(path = "/users/{userId}/activities")
	@Operation(summary = "Get user's activities", 
				parameters = {
			@Parameter(name = "userId", 
						description = "ID of user", 
						required = true)},
				responses = {
            @ApiResponse(description = "Get user's activities successfully", 
            	responseCode = "200",
                content = @Content),
            @ApiResponse(description = "User not found",
            	responseCode = "404",
            	content = @Content),
            @ApiResponse(description = "Something went wrong",
	        	responseCode = "500",
	        	content = @Content)
    })
	public ResponseEntity<Map<String, Object>> getActivitiesByUserId(@PathVariable Long userId, 
		@RequestParam(defaultValue = "0") int page, 
		@RequestParam(defaultValue = "10") int size) {
		
		if(!userService.findById(userId).isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		try {
		      List<Activity> activities = new ArrayList<>();
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Activity> pageActivities = activityService.getActivitiesByUserId(pageable, userId);
		      
		      activities = pageActivities.getContent();
		        
		      Map<String, Object> response = new HashMap<>();
		      response.put("activities", activities);
		      response.put("currentPage", pageActivities.getNumber());
		      response.put("totalItems", pageActivities.getTotalElements());
		      response.put("totalPages", pageActivities.getTotalPages());

		      return new ResponseEntity<>(response, HttpStatus.OK);
      	} catch (Exception e) {
      		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      	}
		
	}
	
}
