package com.pch777.bargains.web;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Comment", description = "Operations about comment")
public class CommentRestController {

	private CommentService commentService;
	private BargainService bargainService;
	private UserService userService;
	private UserSecurity userSecurity;
	private ActivityService activityService;
	
	@GetMapping("/comments")
	@Operation(summary = "Get all comments", 
				responses = {
            @ApiResponse(description = "Get all comments successfully", 
            	responseCode = "200",
                content = @Content),
            @ApiResponse(description = "Something went wrong",
            	responseCode = "500",
            	content = @Content)
    })
	public ResponseEntity<Map<String, Object>> getComments( 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
	      List<Comment> comments = new ArrayList<>();
	      
	      Pageable pageable = PageRequest.of(page, size);
	      Page<Comment> pageComments = commentService.getAllComments(pageable); 
	      
	      comments = pageComments.getContent();
	
	      Map<String, Object> response = createResponse(pageComments, comments);
	
	      return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
        
	} 
	
	@GetMapping("/bargains/{bargainId}/comments")
	@Operation(summary = "Get bargain's comments", 
				parameters = {
			@Parameter(name = "bargainId", 
						description = "ID of bargain", 
						required = true)},
				responses = {
            @ApiResponse(description = "Get bargain's comments successfully", 
            	responseCode = "200",
                content = @Content),
            @ApiResponse(description = "Bargain not found",
            	responseCode = "404",
            	content = @Content),
            @ApiResponse(description = "Something went wrong",
	        	responseCode = "500",
	        	content = @Content)
    })
	public ResponseEntity<Map<String, Object>> getCommentsByBargainId(@PathVariable Long bargainId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
		if(!bargainService.getById(bargainId).isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
        try {
	      List<Comment> comments = new ArrayList<>();
	      
	      Pageable pageable = PageRequest.of(page, size);
	      Page<Comment> pageComments = commentService.getCommentsByBargainId(pageable, bargainId); 
	      
	      comments = pageComments.getContent();
	
	      Map<String, Object> response = createResponse(pageComments, comments);
	
	      return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
        
	} 
	
	@GetMapping("/users/{userId}/comments")
	@Operation(summary = "Get user's comments", 
				parameters = {
			@Parameter(name = "userId", 
						description = "ID of user", 
						required = true)},
				responses = {
            @ApiResponse(description = "Get user's comments successfully", 
            	responseCode = "200",
                content = @Content),
            @ApiResponse(description = "User not found",
            	responseCode = "404",
            	content = @Content),
            @ApiResponse(description = "Something went wrong",
	        	responseCode = "500",
	        	content = @Content)
    })
	public ResponseEntity<Map<String, Object>> getCommentsByUserId(@PathVariable Long userId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
		if(!userService.findById(userId).isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
        try {
	      List<Comment> comments = new ArrayList<>();
	      
	      Pageable pageable = PageRequest.of(page, size);
	      Page<Comment> pageComments = commentService.getCommentsByUserId(pageable, userId); 
	      
	      comments = pageComments.getContent();
	
	      Map<String, Object> response = createResponse(pageComments, comments);
	
	      return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
        
	} 
	
	private Map<String, Object> createResponse(Page<Comment> pageComments, List<Comment> comments) {
		Map<String, Object> response = new HashMap<>();
		  response.put("comments", comments);
		  response.put("currentPage", pageComments.getNumber());
		  response.put("totalItems", pageComments.getTotalElements());
		  response.put("totalPages", pageComments.getTotalPages());
		return response;
	} 

	@GetMapping("comments/{commentId}")
	@Operation(summary = "Get comment", 
				parameters = {
			@Parameter(name = "commentId", 
						description = "ID of comment", 
						required = true)},
				responses = {
            @ApiResponse(description = "Get comment successfully", 
            	responseCode = "200",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Comment.class))),
            @ApiResponse(description = "Comment not found",
            	responseCode = "404",
            	content = @Content)
    })
	public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
		return commentService
				.getById(commentId)
				.map(comment -> ResponseEntity.ok(comment))
				.orElse(ResponseEntity.notFound().build());
	}

	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PostMapping("/bargains/{bargainId}/comments")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Add comment to the bargain",
				parameters = {
			@Parameter(name = "bargainId", 
						description = "ID of bargain", 
						required = true)},
				requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
						description = "Comment content", required = true,
						content = @Content(schema=@Schema(implementation = String.class),
							mediaType = MediaType.TEXT_PLAIN_VALUE,
					        examples = {	
					        		@ExampleObject(
					        				name = "Add comment",		                											
					                		value = "This is the content of the new comment",
					                		summary = "Example content")})),
				responses = {
            @ApiResponse(description = "Comment successfully added", 
            	responseCode = "201",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(description = "Comment not found",
            	responseCode = "404",
            	content = @Content)
    })
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
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PutMapping("comments/{commentId}")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Edit comment", 
				parameters = {
			@Parameter(name = "commentId", 
						description = "ID of comment", 
						required = true)},
				requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
						        description = "Comment content", required = true,
						        content = @Content(schema=@Schema(implementation = String.class),
						        mediaType = MediaType.TEXT_PLAIN_VALUE,
					                            	examples = {	
					                        			@ExampleObject(
					                        					name = "Edit comment",		                											
					                							value = "This is the content of the edited comment",
					                							summary = "Example content")})),
				responses = {
            @ApiResponse(description = "Comment successfully edited", 
            	responseCode = "200",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(description = "You don't have permission to do it",
            	responseCode = "403",
            	content = @Content),
            @ApiResponse(description = "Comment not found",
        	responseCode = "404",
        	content = @Content)
    })
	public ResponseEntity<Object> updateComment(@RequestBody String content, @PathVariable Long commentId,
			Principal principal) {
		
		return commentService.getById(commentId).map(comment -> {
			if(userSecurity.isOwnerOrAdmin(comment.getUser().getEmail(), principal.getName())) {			
				comment.setContent(content);
				return ResponseEntity.ok().build();
			} 
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		}).orElse(ResponseEntity.notFound().build());

	}

	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@DeleteMapping("comments/{commentId}")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Delete comment", 
				parameters = {
			@Parameter(name = "commentId", 
						description = "ID of comment", 
						required = true)},	
				responses = {
            @ApiResponse(description = "Comment successfully deleted", 
            	responseCode = "204",
                content = @Content),
            @ApiResponse(description = "You don't have permission to do it",
            	responseCode = "403",
            	content = @Content),
            @ApiResponse(description = "Comment not found",
        	responseCode = "404",
        	content = @Content)
    })
	public ResponseEntity<Object> deleteCommentById(@PathVariable Long commentId, Principal principal) {
		return commentService.getById(commentId)
				.map(comment -> {
					if(userSecurity.isOwnerOrAdmin(comment.getUser().getEmail(), principal.getName())) {
						commentService.deleteById(commentId);
						return ResponseEntity.noContent().build();
					} else {
		                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		            }
				}).orElse(ResponseEntity.notFound().build());					
	}		

}
