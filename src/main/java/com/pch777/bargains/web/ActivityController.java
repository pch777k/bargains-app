package com.pch777.bargains.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ActivityController {

	private ActivityService activityService;
	
	@GetMapping("/activities/{id}")
	public ResponseEntity<ActivityResponse> getActivityById(@PathVariable Long id) {
		return activityService.getActivityById(id)
				.map(a -> ResponseEntity.ok(new ActivityResponse(a.getId(),
										 						 a.getBargain().getId(),
										    				     a.getBargain().getTitle(),
										    				     a.getUser().getId(),
										    				     a.getUser().getNickname(),
										    				     a.getActivityType())))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/users/{userId}/activities")
	public ResponseEntity<Map<String, Object>> getActivitiesByUserId(@PathVariable Long userId, 
		@RequestParam(defaultValue = "0") int page, 
		@RequestParam(defaultValue = "10") int size) {
		
		try {
		      List<ActivityResponse> activityResponse = new ArrayList<>();
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Activity> pageActivities = activityService.getActivitiesByUserId(pageable, userId);
		      
		      activityResponse = pageActivities.getContent()
		    		  .stream()
		    		  .map(a -> new ActivityResponse(a.getId(),
		    				  						 a.getBargain().getId(),
							    				     a.getBargain().getTitle(),
							    				     a.getUser().getId(),
							    				     a.getUser().getNickname(),
							    				     a.getActivityType()))
		    		  .collect(Collectors.toList());
		        
		      Map<String, Object> response = new HashMap<>();
		      response.put("activities", activityResponse);
		      response.put("currentPage", pageActivities.getNumber());
		      response.put("totalItems", pageActivities.getTotalElements());
		      response.put("totalPages", pageActivities.getTotalPages());

		      return new ResponseEntity<>(response, HttpStatus.OK);
      	} catch (Exception e) {
      		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      	}
		
	}
	
}
