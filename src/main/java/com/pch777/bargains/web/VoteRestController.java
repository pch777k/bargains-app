package com.pch777.bargains.web;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.bargains.dto.VoteDto;
import com.pch777.bargains.exception.NotFoundException;
import com.pch777.bargains.model.Vote;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.VoteService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/votes")
@RestController
@Tag(name = "Vote", description = "Operations about vote")
public class VoteRestController {

	private VoteService voteService;
	private BargainService bargainService;
	private UserSecurity userSecurity;

	@GetMapping
	@Operation(summary = "Get all votes", responses = {
            @ApiResponse(description = "Get votes successfully", 
            	responseCode = "200",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Vote.class)))
    })
	public List<Vote> getAllVotes() {
		return voteService.getAllVotes();
	}

	@GetMapping("/{voteId}")
	@Operation(summary = "Get vote", 
				parameters = {
			@Parameter(name = "voteId", 
				description = "ID of vote", required = true)},
				responses = {
            @ApiResponse(description = "Get vote successfully", 
            	responseCode = "200",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Vote.class))),
            @ApiResponse(description = "Vote not found",
            	responseCode = "404",
            	content = @Content)
    })
	public ResponseEntity<Vote> getVoteById(@PathVariable Long voteId) throws NotFoundException {
		return voteService.getById(voteId)
				.map(ResponseEntity::ok)				
				.orElseThrow(() -> new NotFoundException("Vote with id " + voteId + " not found"));
	}

	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PostMapping("/{bargainId}")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Vote on to the bargain", 
				parameters = {
			@Parameter(name = "bargainId", 
					description = "ID of bargain that can be voted on", 
					required = true)},
				requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			        description = "VoteType Object", required = true,
			        content = @Content(schema=@Schema(implementation = VoteDto.class),
		                            	mediaType = MediaType.APPLICATION_JSON_VALUE,
		                            	examples = {	
		                        			@ExampleObject(
		                        					name = "Vote for the bargain: UPVOTE",		                											
		                							value = "{\"voteType\": \"UPVOTE\"}",
		                							summary = "UPVOTE"),
		                					@ExampleObject(
		                							name = "Vote against the bargain: DOWNVOTE",
		                							value = "{\"voteType\": \"DOWNVOTE\"}",
		                							summary = "DOWNVOTE")})),
				responses = {
			@ApiResponse(description = "Vote successfully added", 
				responseCode = "202", 
			    content = @Content),
			@ApiResponse(description = "Bad request", 
				responseCode = "400",
				content = @Content),
			@ApiResponse(description = "Bargain not found",
				responseCode = "404",
				content = @Content)
	})
	public ResponseEntity<String> vote(@PathVariable("bargainId") Long bargainId, 
			@RequestBody VoteDto voteDto) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		if(voteDto.getVoteType() != null) {
			return bargainService.getById(bargainId).map(bargain -> {
				if(voteService.vote(voteDto, bargainId, email)) {
				   return new ResponseEntity<>("Vote successfully added",HttpStatus.ACCEPTED);
				}
				
				return new ResponseEntity<>("User has already voted on this bargain or user is an owner", HttpStatus.BAD_REQUEST);
	
			}).orElse(new ResponseEntity<>("Bargain with id " + bargainId + " not found", HttpStatus.NOT_FOUND));

		}
		return ResponseEntity.badRequest().body("Wrong input, voteType field cannot be null");

	}

	@DeleteMapping("/{id}")
	@Hidden
	public ResponseEntity<Object> deleteVoteById(@PathVariable Long id, Principal principal) {
		return voteService.getById(id).map(v -> {
				if(userSecurity.isOwnerOrAdmin(v.getUser().getEmail(), principal.getName())) {
					voteService.deleteVoteById(id);
					return ResponseEntity.noContent().build();
				} else {
	                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	            }
			}).orElse(ResponseEntity.notFound().build());				
	}

}
