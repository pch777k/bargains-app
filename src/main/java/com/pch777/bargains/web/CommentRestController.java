package com.pch777.bargains.web;

import java.net.URI;
import java.security.Principal;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Comment;
import com.pch777.bargains.model.User;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.CommentService;
import com.pch777.bargains.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CommentRestController {

	private CommentService commentService;
	private BargainService bargainService;
	private UserService userService;
	private UserSecurity userSecurity;
	private ActivityService activityService;
	
	@GetMapping("/comments")
	public ResponseEntity<Map<String, Object>> getComments( 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
	      List<CommentResponse> comments = new ArrayList<>();
	      
	      Pageable pageable = PageRequest.of(page, size);
	      Page<Comment> pageComments = commentService.getAllComments(pageable); 
	      
	      comments = pageComments.getContent()
	    		  .stream()
	    		  .map(c -> new CommentResponse(c.getId(),
												c.getCreatedAt(),
												c.getUpdatedAt(),
												c.getContent(),
												c.getBargain().getId(),
												c.getBargain().getTitle(),
												c.getUser().getId(),
												c.getUser().getNickname()))
	    		  .collect(Collectors.toList());
	
	      Map<String, Object> response = new HashMap<>();
	      response.put("comments", comments);
	      response.put("currentPage", pageComments.getNumber());
	      response.put("totalItems", pageComments.getTotalElements());
	      response.put("totalPages", pageComments.getTotalPages());
	
	      return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
        
	} 
	
	@GetMapping("/bargains/{bargainId}/comments")
	public ResponseEntity<Map<String, Object>> getCommentsByBargainId(@PathVariable Long bargainId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
	      List<CommentResponse> comments = new ArrayList<>();
	      
	      Pageable pageable = PageRequest.of(page, size);
	      Page<Comment> pageComments = commentService.getCommentsByBargainId(pageable, bargainId); 
	      
	      comments = pageComments.getContent()
	    		  .stream()
	    		  .map(c -> new CommentResponse(c.getId(),
												c.getCreatedAt(),
												c.getUpdatedAt(),
												c.getContent(),
												c.getBargain().getId(),
												c.getBargain().getTitle(),
												c.getUser().getId(),
												c.getUser().getNickname()))
	    		  .collect(Collectors.toList());
	
	      Map<String, Object> response = new HashMap<>();
	      response.put("comments", comments);
	      response.put("currentPage", pageComments.getNumber());
	      response.put("totalItems", pageComments.getTotalElements());
	      response.put("totalPages", pageComments.getTotalPages());
	
	      return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
        
	} 
	
	@GetMapping("/users/{userId}/comments")
	public ResponseEntity<Map<String, Object>> getCommentsByUserId(@PathVariable Long userId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
	      List<CommentResponse> comments = new ArrayList<>();
	      
	      Pageable pageable = PageRequest.of(page, size);
	      Page<Comment> pageComments = commentService.getCommentsByUserId(pageable, userId); 
	      
	      comments = pageComments.getContent()
	    		  .stream()
	    		  .map(c -> new CommentResponse(c.getId(),
												c.getCreatedAt(),
												c.getUpdatedAt(),
												c.getContent(),
												c.getBargain().getId(),
												c.getBargain().getTitle(),
												c.getUser().getId(),
												c.getUser().getNickname()))
	    		  .collect(Collectors.toList());
	
	      Map<String, Object> response = new HashMap<>();
	      response.put("comments", comments);
	      response.put("currentPage", pageComments.getNumber());
	      response.put("totalItems", pageComments.getTotalElements());
	      response.put("totalPages", pageComments.getTotalPages());
	
	      return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
        
	} 

	@GetMapping("comments/{id}")
	public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id) {
		return commentService
				.getById(id)
				.map(c -> ResponseEntity.ok(new CommentResponse(c.getId(),
										  						c.getCreatedAt(),
										  						c.getUpdatedAt(),
										  						c.getContent(),
										  						c.getBargain().getId(),
										  						c.getBargain().getTitle(),
										  						c.getUser().getId(),
										  						c.getUser().getNickname())))
				.orElse(ResponseEntity.notFound().build());
	}

	@Transactional
	@PostMapping("/bargains/{bargainId}/comments")
	public ResponseEntity<Object> addComment(@RequestBody String content, 
			@PathVariable Long bargainId, Principal principal) {

		return bargainService.getById(bargainId).map(bargain -> {
			User user = userService.findUserByEmail(principal.getName());
			Comment comment = new Comment();
			comment.setContent(content);
			comment.setUser(user);
			comment.setBargain(bargain);
		    commentService.addComment(comment);
		    activityService.addActivity(comment.getUser(), comment.getCreatedAt(), comment.getBargain(), ActivityType.COMMENT);
	
		    URI uri = ServletUriComponentsBuilder
			.fromCurrentRequestUri()
			.path("/" + comment.getId().toString())
			.build()
			.toUri();
	return ResponseEntity.created(uri).build();
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

	}

	@Transactional
	@PutMapping("comments/{id}")
	public ResponseEntity<Object> updateComment(@RequestBody String content, @PathVariable Long id,
			Principal principal) {
		
		return commentService.getById(id).map(comment -> {
			if(userSecurity.isOwnerOrAdmin(comment.getUser().getEmail(), principal.getName())) {			
				comment.setContent(content);
				return ResponseEntity.ok().build();
			} 
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		}).orElse(ResponseEntity.notFound().build());

	}

	@Transactional
	@DeleteMapping("comments/{id}")
	public ResponseEntity<Object> deleteCommentById(@PathVariable Long id, Principal principal) {
		return commentService.getById(id)
				.map(c -> {
					if(userSecurity.isOwnerOrAdmin(c.getUser().getEmail(), principal.getName())) {
						commentService.deleteById(id);
						return ResponseEntity.noContent().build();
					} else {
		                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		            }
				}).orElse(ResponseEntity.notFound().build());					
	}		

}
