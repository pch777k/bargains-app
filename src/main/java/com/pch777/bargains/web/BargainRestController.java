package com.pch777.bargains.web;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pch777.bargains.dto.BargainDto;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.BargainPhoto;
import com.pch777.bargains.model.Category;
import com.pch777.bargains.model.User;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainPhotoService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.UserService;
import com.pch777.bargains.utility.StringToEnumConverter;
import com.pch777.bargains.validation.FileSize;
import com.pch777.bargains.validation.ImageContentType;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Bargain", description = "Operations about Bargain")
public class BargainRestController {

	private BargainService bargainService;
	private BargainPhotoService bargainPhotoService;
	private UserService userService;
	private UserSecurity userSecurity;
	private ActivityService activityService;
	private StringToEnumConverter converter;

	@GetMapping("/bargains")
	@Operation(summary = "Get all bargains", 
				responses = {
            @ApiResponse(description = "Get bargains successfully", 
            	responseCode = "200",
                content = @Content)
    })
	public ResponseEntity<Map<String, Object>> getBargains( 
		@RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
		      List<Bargain> bargains;
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLike(pageable, keyword);
		      
		      bargains = pageBargains.getContent();
		
		      Map<String, Object> response = createResponse(pageBargains, bargains);

		      return new ResponseEntity<>(response, HttpStatus.OK);
        	} catch (Exception e) {
        		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        	}
        
	} 
	
	@GetMapping("/users/{userId}/bargains")
	@Operation(summary = "Get user's bargains", 
				parameters = {
			@Parameter(name = "userId", 
						description = "ID of user", 
						required = true)},
				responses = {
			@ApiResponse(description = "Get user's bargains successfully", 
				responseCode = "200",
			    content = @Content),
			@ApiResponse(description = "User not found",
				responseCode = "404",
				content = @Content)
	})
	public ResponseEntity<Map<String, Object>> getBargainsByTitleLikeByUserId(@PathVariable Long userId, 
		@RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
		      List<Bargain> bargains;
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLikeByUserId(pageable, keyword, userId);
		      
		      bargains = pageBargains.getContent();
		
		      Map<String, Object> response = createResponse(pageBargains, bargains);

		      return new ResponseEntity<>(response, HttpStatus.OK);
        	} catch (Exception e) {
        		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        	}
        
	}
	
	@Operation(summary = "Get all bargains of specific category", 
			parameters = {
		@Parameter(name = "category", 
					description = "name of category", 
					required = true)},
			responses = {
		@ApiResponse(description = "Get bargains successfully", 
			responseCode = "200",
		    content = @Content),
		@ApiResponse(description = "Category not found",
			responseCode = "404",
			content = @Content)
	})
	@GetMapping("/bargains/category/{category}")
	public ResponseEntity<Map<String, Object>> getBargainsByTitleLikeByCategory(@PathVariable String category, 
		@RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
        	Category bargainCategory = converter.convert(category);  
        	
        	List<Bargain> bargains;
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLikeByCategory(pageable, keyword, bargainCategory);
		      
		      bargains = pageBargains.getContent();
		
		      Map<String, Object> response = createResponse(pageBargains, bargains);

		      return new ResponseEntity<>(response, HttpStatus.OK);
        	} catch (Exception e) {
        		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        	}       
	} 
	
	private Map<String, Object> createResponse(Page<Bargain> pageBargains, List<Bargain> bargains) {
		Map<String, Object> response = new HashMap<>();
		  response.put("bargains", bargains);
		  response.put("currentPage", pageBargains.getNumber());
		  response.put("totalItems", pageBargains.getTotalElements());
		  response.put("totalPages", pageBargains.getTotalPages());
		return response;
	} 

	@GetMapping("/bargains/{bargainId}")
	@Operation(summary = "Get bargain", 
				parameters = {
			@Parameter(name = "bargainId", 
						description = "ID of bargain", 
						required = true)},
				responses = {
			@ApiResponse(description = "Get bargain successfully", 
				responseCode = "200",
			    content = @Content(mediaType = "application/json",
			    schema = @Schema(implementation = Bargain.class))),
			@ApiResponse(description = "Bargain not found",
				responseCode = "404",
				content = @Content)
	})
	public ResponseEntity<Bargain> getBargainById(@PathVariable Long bargainId) {
		return bargainService.getById(bargainId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PostMapping("/bargains")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Add bargain",
				requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
						description = "BargainDto object, title and category fields are required", required = true,
						content = @Content(schema=@Schema(implementation = BargainDto.class),
							mediaType = MediaType.APPLICATION_JSON_VALUE,
					        examples = {	
					        		@ExampleObject(
					        				name = "Add bargain",		                											
					                		value = "{"
						                				+ "\"title\": \"Darth Vader™ Helmet 75304 Star Wars LEGO\","
						                				+ "\"description\": \"Lorem ipsum\","
						                				+ "\"reducePrice\": 205.13,"
						                				+ "\"normalPrice\": 329.99,"
						                				+ "\"delivery\": 0,"
						                				+ "\"coupon\": \"lego\","
						                				+ "\"link\": \"https://www.amazon.pl/dp/B08G4GPS3Q/ref=cm_sw_r_cp_apa_glt_i_MXEQJV0KF8FF04EZRK70\","
						                				+ "\"closed\": false,"
						                				+ "\"startBargain\": \"\","
						                				+ "\"endBargain\": \"\","
						                				+ "\"category\": \"FAMILY\","
						                				+ "\"shop\": {"
								                			+ "\"id\": 5,"
								                			+ "\"name\": \"Amazon.pl\","
								                			+ "\"webpage\": \"https://www.amazon.pl\""
								                			+ "}" 
								                	+ "}",
					                		summary = "Example bargain")})),
				responses = {
			@ApiResponse(description = "Bargain successfully added", 
				responseCode = "201",
			    content = @Content),
			@ApiResponse(description = "Invalid input",
				responseCode = "400",
				content = @Content),
			@ApiResponse(description = "Unauthorized", 
				responseCode = "401",
				content = @Content)
	})
	public ResponseEntity<Void> addBargain(@Valid @RequestBody BargainDto bargainDto) throws ResourceNotFoundException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Bargain bargain = bargainService.bargainDtoToBargain(bargainDto);
		BargainPhoto bargainPhoto = bargainPhotoService.getBargainPhotoById(1L)
				.orElseThrow(ResourceNotFoundException::new);
		bargain.setBargainPhoto(bargainPhoto);
		
		User user = userService.findUserByEmail(authentication.getName());
		bargain.setUser(user);
		bargainService.addBargain(bargain);
		activityService.addActivity(bargain.getUser(), bargain.getCreatedAt(), bargain, ActivityType.BARGAIN);
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequestUri()
				.path("/" + bargain.getId().toString())
				.build()
				.toUri();
		return ResponseEntity.created(uri).build();

	}

	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PutMapping("/bargains/{bargainId}")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Update bargain",
				parameters = {
			@Parameter(name = "bargainId", 
						description = "ID of bargain", 
						required = true)},
				requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
						description = "BargainDto object, title and category fields are required", required = true,
						content = @Content(schema=@Schema(implementation = BargainDto.class),
							mediaType = MediaType.APPLICATION_JSON_VALUE,
					        examples = {	
					        		@ExampleObject(
					        				name = "Update bargain",		                											
					                		value = "{"
						                				+ "\"title\": \"Intel Core i9-9900K BX80684I99900K\","
						                				+ "\"description\": \"Lorem ipsum\","
						                				+ "\"reducePrice\": 2099.99,"
						                				+ "\"normalPrice\": 2599.99,"
						                				+ "\"delivery\": 0,"
						                				+ "\"coupon\": \"intel\","
						                				+ "\"link\": \"https://www.amazon.pl/gp/product/B005404P9I/\","
						                				+ "\"closed\": false,"
						                				+ "\"startBargain\": \"\","
						                				+ "\"endBargain\": \"\","
						                				+ "\"category\": \"ELECTRONICS\","
						                				+ "\"shop\": {"
								                			+ "\"id\": 5,"
								                			+ "\"name\": \"Amazon.pl\","
								                			+ "\"webpage\": \"https://www.amazon.pl\""
								                			+ "}" 
								                	+ "}",
					                		summary = "Example bargain")})),
				responses = {
			@ApiResponse(description = "Bargain successfully updated", 
				responseCode = "202",
			    content = @Content),
			@ApiResponse(description = "Invalid input",
				responseCode = "400",
				content = @Content)
	})
	public ResponseEntity<Object> updateBargain(@Valid @RequestBody BargainDto bargainDto, @PathVariable Long bargainId, 
			Principal principal) {
		
		return bargainService.getById(bargainId)
				.map(bargain -> {
                    if (userSecurity.isOwnerOrAdmin(bargain.getUser().getEmail(), principal.getName())) {
                    	bargain.setTitle(bargainDto.getTitle());
                    	bargain.setDescription(bargainDto.getDescription());
                    	bargain.setReducePrice(bargainDto.getReducePrice());
                    	bargain.setNormalPrice(bargainDto.getNormalPrice());
                    	bargain.setDelivery(bargainDto.getDelivery());
                    	bargain.setCoupon(bargainDto.getCoupon());
                    	bargain.setLink(bargainDto.getLink());
                    	bargain.setStartBargain(bargainDto.getStartBargain());
                    	bargain.setEndBargain(bargainDto.getEndBargain());
                    	bargain.setClosed(bargainDto.getClosed());
                    	bargain.setCategory(bargainDto.getCategory());
                    	bargain.setShop(bargainDto.getShop());
                        return ResponseEntity.accepted().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());
	} 
 	
	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PatchMapping("/bargains/{id}")
	@Hidden
	public ResponseEntity<Object> updateBargainTitle(@RequestBody String title, @PathVariable Long id, Principal principal) {
	
		return bargainService.getById(id)
				.map(b -> {
                    if (userSecurity.isOwnerOrAdmin(b.getUser().getEmail(), principal.getName())) {
                    	b.setTitle(title);
                        return ResponseEntity.accepted().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());	
	}

	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@DeleteMapping("/bargains/{bargainId}")	
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Delete bargain", 
				parameters = {
			@Parameter(name = "bargainId", 
						description = "ID of bargain", 
						required = true)},	
				responses = {
			@ApiResponse(description = "Bargain successfully deleted", 
				responseCode = "204",
			    content = @Content),
			@ApiResponse(description = "You don't have permission to do it",
				responseCode = "403",
				content = @Content),
			@ApiResponse(description = "Bargain not found",
			responseCode = "404",
			content = @Content)
	})
	public ResponseEntity<Object> deleteBargainById(@PathVariable Long bargainId, Principal principal) {
		
		return bargainService.getById(bargainId)
				.map(bargain -> {
                    if (userSecurity.isOwnerOrAdmin(bargain.getUser().getEmail(), principal.getName())) {
                    	bargainService.deleteBargainById(bargainId);
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());	
	}
	
	@GetMapping("/all/bargains")
	@Hidden
	public List<Bargain> getAllBargains() {
		return bargainService.getAllBargains();
	}
	
	@GetMapping("/bargains/photo/{photoId}")
	@Hidden
	public ResponseEntity<Resource> getPhoto(@PathVariable Long photoId) {
		return bargainPhotoService.getBargainPhotoById(photoId)
				.map(file -> {
					String contentDisposition = "attachment; filname=\"" + file.getFilename() + "\"";
					byte[] bytes = file.getFile();
					Resource resource = new ByteArrayResource(bytes);
					return ResponseEntity
						.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
						.contentType(MediaType.parseMediaType(file.getContentType()))
						.body(resource);
				}).orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/bargains/{bargainId}/photo")
	@Operation(summary = "Get bargain's photo", 
				parameters = {
			@Parameter(name = "bargainId", description = "ID of bargain to get the photo", required = true)},			
				responses = {
			@ApiResponse(description = "Get bargain's photo successfully", 
				responseCode = "200",
			    content = @Content),             
			@ApiResponse(description = "Bargain not found",
				responseCode = "404",
				content = @Content)
	})
	public ResponseEntity<Resource> getBargainPhoto(@PathVariable Long bargainId) {
		return bargainService.getById(bargainId)
				.map(bargain -> { 
					return bargainPhotoService.getBargainPhotoById(bargain.getBargainPhoto().getId())
							.map(file -> {
								String contentDisposition = "attachment; filname=\"" + file.getFilename() + "\"";
								byte[] bytes = file.getFile();
								Resource resource = new ByteArrayResource(bytes);
								return ResponseEntity
									.ok()
									.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
									.contentType(MediaType.parseMediaType(file.getContentType()))
									.body(resource);
							}).orElse(ResponseEntity.notFound().build());		
				}).orElse(ResponseEntity.notFound().build());
	}
	
	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PostMapping(value = "/bargains/{bargainId}/photo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Uploads bargain's photo", 
				parameters = {
			@Parameter(name = "bargainId", description = "ID of bargain to update the photo", required = true)},
				responses = {
			@ApiResponse(description = "Bargain's photo successfully updated", 
				responseCode = "202", content = @Content),
			@ApiResponse(description = "You don't have permission to do it",
				responseCode = "403", content = @Content),
			@ApiResponse(description = "Bargain not found",
			responseCode = "404", content = @Content)
	})
	public ResponseEntity<Object> addPhotoToBargain(@PathVariable Long bargainId, 
			@Valid @ImageContentType @FileSize @RequestParam(value = "file") MultipartFile multipartFile, Principal principal) {

		return bargainService.getById(bargainId)
				.map(bargain -> {
                    if (userSecurity.isOwnerOrAdmin(bargain.getUser().getEmail(), principal.getName())) {
                    	if(!multipartFile.isEmpty()) {
							try {
								BargainPhoto bargainPhoto = bargainService.fileToBargainPhoto(multipartFile);
								bargainPhotoService.saveBargainPhoto(bargainPhoto);
								bargain.setBargainPhoto(bargainPhoto); 
							} catch (IOException e) {
								log.error(e.getMessage());
							}	  		
                    	}                  	
                        return ResponseEntity.accepted().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());
	}
	
}
