package com.pch777.bargains.web;

import java.security.Principal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.bargains.model.VoteDto;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.VoteService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/votes")
@RestController
public class VoteRestController {

	private VoteService voteService;
	private BargainService bargainService;
	private UserSecurity userSecurity;

	@GetMapping
	public List<VoteResponse> getAllVotes() {
		return voteService.getAllVotes()
				.stream()
				.map(v -> new VoteResponse(v.getId(),
										   v.getVoteType(),
										   v.getCreatedAt(),
										   v.getBargain().getId(),
										   v.getBargain().getTitle(),
										   v.getUser().getId(),
										   v.getUser().getNickname()))
				.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<VoteResponse> getVoteById(@PathVariable Long id) {
		return voteService.getById(id)
				.map(v -> ResponseEntity.ok(new VoteResponse(v.getId(),
														     v.getVoteType(),
														     v.getCreatedAt(),
														     v.getBargain().getId(),
														     v.getBargain().getTitle(),
														     v.getUser().getId(),
														     v.getUser().getNickname())))				
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/{bargainId}")
	public ResponseEntity<Object> vote(@PathVariable("bargainId") Long bargainId, @RequestBody VoteDto voteDto) {

		return bargainService.getById(bargainId).map(bargain -> {
			if(voteService.vote(voteDto, bargainId)) {
			   return ResponseEntity.accepted().build();
			}
			
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Map<String, Object> body = responseError(status,
					"User has already voted on this bargain or user is an owner", "/api/votes");

			return new ResponseEntity<Object>(body, status);

		}).orElse(ResponseEntity.notFound().build());

	}

	@DeleteMapping("/{id}")
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

	public Map<String, Object> responseError(HttpStatus status, String message, String path) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", status.value());
		body.put("error", status.name());
		body.put("message", message);
		body.put("path", path);
		return body;
	}

}
