package com.pch777.bargains.web;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.ActivityType;
import com.pch777.bargains.model.Bargain;
import com.pch777.bargains.model.BargainDto;
import com.pch777.bargains.model.BargainPhoto;
import com.pch777.bargains.model.Category;
import com.pch777.bargains.model.User;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.ActivityService;
import com.pch777.bargains.service.BargainPhotoService;
import com.pch777.bargains.service.BargainService;
import com.pch777.bargains.service.UserService;
import com.pch777.bargains.utility.StringToEnumConverter;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BargainRestController {

	private BargainService bargainService;
	private BargainPhotoService bargainPhotoService;
	private UserService userService;
	private UserSecurity userSecurity;
	private ActivityService activityService;
	private StringToEnumConverter converter;

	@GetMapping("/bargains")
	public ResponseEntity<Map<String, Object>> getBargains( 
		@RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
		      List<Bargain> bargains = new ArrayList<>();
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLike(pageable, keyword);
		      
		      bargains = pageBargains.getContent();
		
		      Map<String, Object> response = new HashMap<>();
		      response.put("bargains", bargains);
		      response.put("currentPage", pageBargains.getNumber());
		      response.put("totalItems", pageBargains.getTotalElements());
		      response.put("totalPages", pageBargains.getTotalPages());

		      return new ResponseEntity<>(response, HttpStatus.OK);
        	} catch (Exception e) {
        		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        	}
        
	} 
	
	@GetMapping("/users/{userId}/bargains")
	public ResponseEntity<Map<String, Object>> getBargainsByTitleLikeByUserId(@PathVariable Long userId, 
		@RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
		      List<Bargain> bargains = new ArrayList<>();
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLikeByUserId(pageable, keyword, userId);
		      
		      bargains = pageBargains.getContent();
		
		      Map<String, Object> response = new HashMap<>();
		      response.put("bargains", bargains);
		      response.put("currentPage", pageBargains.getNumber());
		      response.put("totalItems", pageBargains.getTotalElements());
		      response.put("totalPages", pageBargains.getTotalPages());

		      return new ResponseEntity<>(response, HttpStatus.OK);
        	} catch (Exception e) {
        		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        	}
        
	} 
	
	@GetMapping("/bargains/category/{category}")
	public ResponseEntity<Map<String, Object>> getBargainsByTitleLikeByUserId(@PathVariable String category, 
		@RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {
        	Category bargainCategory = converter.convert(category);  
        	
        	List<Bargain> bargains = new ArrayList<>();
		      
		      Pageable pageable = PageRequest.of(page, size);
		      Page<Bargain> pageBargains = bargainService.getAllBargainsByTitleLikeByCategory(pageable, keyword, bargainCategory);
		      
		      bargains = pageBargains.getContent();
		
		      Map<String, Object> response = new HashMap<>();
		      response.put("bargains", bargains);
		      response.put("currentPage", pageBargains.getNumber());
		      response.put("totalItems", pageBargains.getTotalElements());
		      response.put("totalPages", pageBargains.getTotalPages());

		      return new ResponseEntity<>(response, HttpStatus.OK);
        	} catch (Exception e) {
        		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        	}
        
	} 

	@GetMapping("/bargains/{id}")
	public ResponseEntity<Bargain> getBargainById(@PathVariable Long id) {
		return bargainService.getById(id)
				.map(bargain -> ResponseEntity.ok(bargain))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping(value = "/bargains", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	@Transactional
	public ResponseEntity<Void> addBargain(@Valid @RequestPart("bargainDto") BargainDto bargainDto,
			@RequestPart("fileImage") MultipartFile multipartFile) throws IOException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Bargain bargain = bargainService.bargainDtoToBargain(bargainDto);
		BargainPhoto bargainPhoto = new BargainPhoto();
		if(multipartFile.isEmpty()) {
			bargain.setBargainPhotoId(1L);
		} else {
			bargainPhoto = bargainService.FileToBargainPhoto(multipartFile);
			bargainPhotoService.saveBargainPhoto(bargainPhoto);
			bargain.setBargainPhotoId(bargainPhoto.getId());
		}
			
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

	@PutMapping(value = "/bargains/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	@Transactional
	public ResponseEntity<Object> updateBargain(@Valid @RequestPart BargainDto bargainDto, @PathVariable Long id, 
			@RequestPart("fileImage") MultipartFile multipartFile, Principal principal) throws IOException {
		
		return bargainService.getById(id)
				.map(b -> {
                    if (userSecurity.isOwnerOrAdmin(b.getUser().getEmail(), principal.getName())) {
                    	if(!multipartFile.isEmpty()) {
                    		BargainPhoto bargainPhoto;
							try {
								bargainPhoto = bargainService.FileToBargainPhoto(multipartFile);
								bargainPhotoService.saveBargainPhoto(bargainPhoto);
	                			b.setBargainPhotoId(bargainPhoto.getId()); 
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}	  		
                    	}
                    	b.setTitle(bargainDto.getTitle());
                    	b.setDescription(bargainDto.getDescription());
                    	b.setReducePrice(bargainDto.getReducePrice());
                    	b.setNormalPrice(bargainDto.getNormalPrice());
        				b.setDelivery(bargainDto.getDelivery());
        				b.setCoupon(bargainDto.getCoupon());
        				b.setLink(bargainDto.getLink());
        				b.setStartBargain(bargainDto.getStartBargain());
        				b.setEndBargain(bargainDto.getEndBargain());
        				b.setClosed(bargainDto.getClosed());
        				b.setCategory(bargainDto.getCategory());
        				b.setShop(bargainDto.getShop());
                        return ResponseEntity.accepted().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());
	} 
 	
	@PatchMapping("/bargains/{id}")
	@Transactional
	public ResponseEntity<Object> updateBargainTitle(@RequestBody String title, @PathVariable Long id, Principal principal) throws ResourceNotFoundException {
	
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
	@DeleteMapping("/bargains/{id}")	
	public ResponseEntity<Object> deleteBargainById(@PathVariable Long id, Principal principal) {
		
		return bargainService.getById(id)
				.map(b -> {
                    if (userSecurity.isOwnerOrAdmin(b.getUser().getEmail(), principal.getName())) {
                    	bargainService.deleteBargainById(id);
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());	
	}
	
	@GetMapping("/all/bargains")
	public List<Bargain> getAllBargains() {
		return bargainService.getAllBargains();
	}
	
	@GetMapping("/bargains/photo/{id}")
	public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
		return bargainPhotoService.getBargainPhotoById(id)
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
	
	@Transactional
	@PostMapping(value = "/bargains/{id}/photo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Object> addPhotoToBargain(@PathVariable Long id, 
			@RequestPart("file") MultipartFile multipartFile, Principal principal) throws IOException {

		return bargainService.getById(id)
				.map(b -> {
                    if (userSecurity.isOwnerOrAdmin(b.getUser().getEmail(), principal.getName())) {
                    	if(!multipartFile.isEmpty()) {
							try {
								BargainPhoto bargainPhoto = bargainService.FileToBargainPhoto(multipartFile);
								bargainPhotoService.saveBargainPhoto(bargainPhoto);
	                			b.setBargainPhotoId(bargainPhoto.getId()); 
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}	  		
                    	}                  	
                        return ResponseEntity.accepted().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());
	}
	
//	@PostMapping("/bargains/photo")
//	public ResponseEntity<Void> addPhoto(@RequestParam("file") MultipartFile multipartFile) throws IOException {
//		BargainPhoto bargainPhoto = bargainService.FileToBargainPhoto(multipartFile);
//		bargainPhotoService.saveBargainPhoto(bargainPhoto);
//		
//		URI uri = ServletUriComponentsBuilder
//				.fromCurrentRequestUri()
//				.path("/" + bargainPhoto.getId().toString())
//				.build()
//				.toUri();
//		return ResponseEntity.created(uri).build();
//	}
	

}
