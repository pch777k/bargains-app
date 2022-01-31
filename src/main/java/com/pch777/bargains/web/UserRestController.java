package com.pch777.bargains.web;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pch777.bargains.exception.EntityFieldException;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.NicknameDto;
import com.pch777.bargains.model.PasswordDto;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.UserDto;
import com.pch777.bargains.model.UserPhoto;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.UserPhotoService;
import com.pch777.bargains.service.UserService;
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

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "Operations about user")
@Validated
public class UserRestController {

	private UserService userService;
	private UserSecurity userSecurity;
	private UserPhotoService userPhotoService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/users")
	@Operation(summary = "Get all users", 
				responses = {
            @ApiResponse(description = "Get all users successfully", 
            	responseCode = "200",
                content = @Content),
            @ApiResponse(description = "Something went wrong",
            	responseCode = "500",
            	content = @Content)
    })
	public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		try {
			List<User> users = new ArrayList<>();

			Pageable pageable = PageRequest.of(page, size);
			Page<User> pageUsers = userService.getUsers(pageable);

			users = pageUsers.getContent();

			Map<String, Object> response = new HashMap<>();
			response.put("users", users);
			response.put("currentPage", pageUsers.getNumber());
			response.put("totalItems", pageUsers.getTotalElements());
			response.put("totalPages", pageUsers.getTotalPages());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/users/{userId}")
	@Operation(summary = "Get user", 
				parameters = {
			@Parameter(name = "userId", description = "ID of user", required = true)},
				responses = {
            @ApiResponse(description = "Get user successfully", 
            	responseCode = "200",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = User.class))),
            @ApiResponse(description = "User not found",
            	responseCode = "404",
            	content = @Content)
    })
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
		return userService.findById(userId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Transactional
	@PostMapping("/users")
	@Operation(summary = "Create user",
				requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "UserDto object", required = true,
					content = @Content(schema=@Schema(implementation = UserDto.class),
						mediaType = MediaType.APPLICATION_JSON_VALUE,
				        examples = {	
				        		@ExampleObject(
				        				name = "Create user",		                											
				                		value = "{"
					                				+ "\"nickname\": \"nick\","
					                				+ "\"email\": \"nick@demomail.com\","
					                				+ "\"password\": \"pass123\","
					                				+ "\"confirmPassword\": \"pass123\""					                				
							                	+ "}",
				                		summary = "Example user")})),
				responses = {
            @ApiResponse(description = "User successfully created", 
            	responseCode = "201",
                content = @Content),
            @ApiResponse(description = "User with email or nickname exists",
            	responseCode = "400",
            	content = @Content)
    })
	public ResponseEntity<String> createUser(@Valid @RequestBody UserDto userDto) throws EntityFieldException, ResourceNotFoundException {
		if (userService.isUserPresent(userDto.getEmail())) {
			throw new EntityFieldException("Email " + userDto.getEmail() + " already exists","email");
		}
		if(userService.isUserNicknamePresent(userDto.getNickname())) {
			throw new EntityFieldException("Nickname " + userDto.getNickname() + " already exists","nickname");
		}

		User user = userService.userDtoToUser(userDto);
		userService.registerUser(user);

		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequestUri()
				.path("/" + user.getId().toString())
				.build()
				.toUri();
		return ResponseEntity.created(uri).build();
	}

	@Transactional
	@PostMapping("/admin")
	@Hidden
	public ResponseEntity<Void> addAdmin(@Valid @RequestBody User admin) throws EntityFieldException {
		if (userService.isUserPresent(admin.getEmail())) {
			throw new EntityFieldException("Email " + admin.getEmail() + " already exists","email");
		}
		if(userService.isUserNicknamePresent(admin.getNickname())) {
			throw new EntityFieldException("Nickname " + admin.getNickname() + " already exists","nickname");
		}
		userService.registerAdmin(admin);
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequestUri()
				.path("/" + admin.getId().toString())
				.build()
				.toUri();
		return ResponseEntity.created(uri).build();
	}

	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PutMapping("users/{userId}/nickname")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Change nickname",
				requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
						description = "NicknameDto object", required = true,
						content = @Content(schema=@Schema(implementation = NicknameDto.class),
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						examples = {	
								@ExampleObject(
										name = "Change nickname",		                											
							            value = "{ \"nickname\": \"nick\"}",
							            summary = "Example nickname")})),
				responses = {
			@ApiResponse(description = "Nickname successfully changed", 
            	responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(description = "User with nickname exists",
            	responseCode = "400", content = @Content),
            @ApiResponse(description = "You don't have permission to do it",
        		responseCode = "403", content = @Content),
            @ApiResponse(description = "User not found",
        		responseCode = "404", content = @Content)
    })
	public ResponseEntity<String> changeNickname(@PathVariable Long userId,
			 @Valid @RequestBody NicknameDto nicknameDto, 
			 Principal principal) throws EntityFieldException {

		if (userService.existsById(userId)) {
			User editedUser = userService.findUserById(userId);
			if (userSecurity.isOwnerOrAdmin(editedUser.getEmail(), principal.getName())) {
				if (!userService.isUserNicknamePresent(nicknameDto.getNickname())
						&& !(editedUser.getNickname().equalsIgnoreCase(nicknameDto.getNickname()))) {
							editedUser.setNickname(nicknameDto.getNickname());
					return ResponseEntity.ok().body("Nickname successfully changed");

				} else {
					throw new EntityFieldException("Nickname " + nicknameDto.getNickname() + " already exists","nickname");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		return ResponseEntity.notFound().build();
	}
	
	@Transactional
	@PreAuthorize("hasAuthority('USER')")
	@PutMapping("users/{userId}/password")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Change password", 
				parameters = {
			@Parameter(name = "userId", description = "ID of user to change password", required = true)},
				responses = {
			@ApiResponse(description = "Password successfully changed", 
            	responseCode = "200", content = @Content),
            @ApiResponse(description = "The old password is not correct",
            	responseCode = "400", content = @Content),
            @ApiResponse(description = "You don't have permission to do it",
        		responseCode = "403", content = @Content),
            @ApiResponse(description = "User not found",
        		responseCode = "404", content = @Content)
    })
	public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordDto passwordDto, @PathVariable Long userId,
			Principal principal) throws EntityFieldException {

		if (userService.existsById(userId)) {
			User editedUser = userService.findUserById(userId);
			if (editedUser.getEmail().equals(principal.getName())) {
				if (!bCryptPasswordEncoder.matches(passwordDto.getOldPassword(), editedUser.getPassword())) {
					throw new EntityFieldException("The old password is not correct","oldPassword");
				} else {
					editedUser.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
					return ResponseEntity.ok().body("Password successfully changed");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		return ResponseEntity.notFound().build();
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("users/{userId}")
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Delete user", 
				parameters = {
			@Parameter(name = "userId", description = "ID of user to delete", required = true)},
				responses = {
            @ApiResponse(description = "User successfully deleted", 
            	responseCode = "204", content = @Content(mediaType = "application/json")),
            @ApiResponse(description = "You don't have permission to do it",
            	responseCode = "403", content = @Content),
            @ApiResponse(description = "User not found",
        	responseCode = "404", content = @Content)
    })
	public ResponseEntity<Object> deleteUserById(@PathVariable Long userId, Principal principal) {

		return userService.findById(userId)
				.map(user -> {
					if (userSecurity.isAdmin(principal.getName())) {
						userService.deleteById(userId);
						return ResponseEntity.noContent().build();
					}
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
				})
				.orElse(ResponseEntity.notFound().build());

	}
	
	@Transactional
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@PutMapping(value = "/users/{userId}/uploadPhoto", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@Operation(security = @SecurityRequirement(name = "basicAuth"),
				summary = "Uploads user's photo", 
				parameters = {
			@Parameter(name = "userId", description = "ID of user to update the photo", required = true)},
				responses = {
            @ApiResponse(description = "User's photo successfully updated", 
            	responseCode = "202", content = @Content),
            @ApiResponse(description = "You don't have permission to do it",
            	responseCode = "403", content = @Content),
            @ApiResponse(description = "User not found",
        	responseCode = "404", content = @Content)
    })
	public ResponseEntity<Object> updateUserPhoto(@PathVariable Long userId, Principal principal,
			@Valid @ImageContentType @FileSize @RequestParam("file") MultipartFile multipartFile) {
		
		return userService.findById(userId)
				.map(user -> {
					if (userSecurity.isOwnerOrAdmin(user.getEmail(), principal.getName())) {
						
						try {
							UserPhoto userPhoto = UserPhoto.builder()
									.file(multipartFile.getBytes())
									.filename(multipartFile.getOriginalFilename())
									.contentType(multipartFile.getContentType())
									.createdAt(LocalDate.now())
									.fileLength(multipartFile.getSize())
									.build();
							userPhotoService.saveUserPhoto(userPhoto);
							user.setUserPhoto(userPhoto);
						} catch (IOException e) {
							System.out.println(e.getMessage());
						}
						return ResponseEntity.accepted().build();
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
					}
					
				})
				.orElse(ResponseEntity.notFound().build());		
	}
	
	@GetMapping("/users/photo/{userId}")
	@Operation(summary = "Get user's photo", 
				parameters = {
			@Parameter(name = "userId", description = "ID of user to get the photo", required = true)},			
				responses = {
            @ApiResponse(description = "Get user's photo successfully", 
            	responseCode = "200",
                content = @Content),             
            @ApiResponse(description = "User not found",
            	responseCode = "404",
            	content = @Content)
    })
	public ResponseEntity<Resource> getPhoto(@PathVariable Long userId) {
		return userPhotoService.getUserPhotoById(userId)
				.map(file -> {
					String contentDisposition = "attachment; filname=\"" + file.getFilename() + "\"";
					byte[] bytes = file.getFile();
					Resource resource = new ByteArrayResource(bytes);
					return ResponseEntity
						.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
						.contentType(MediaType.parseMediaType(file.getContentType()))
						.body(resource);
				})
				.orElse(ResponseEntity.notFound().build());
	}

//	@Transactional
//	@PutMapping("users/{id}")
//	public ResponseEntity<Void> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id, Principal principal) throws EntityFieldException {
//		if (userService.existsById(id)) {
//			User editedUser = userService.findUserById(id);
//			
//			if (userSecurity.isOwnerOrAdmin(editedUser.getEmail(), principal.getName())) {
//				if (!userService.isUserPresent(userDto.getEmail())
//						&& !(editedUser.getEmail().equalsIgnoreCase(userDto.getEmail()))) {
//					editedUser.setEmail(userDto.getEmail());
//
//				} else {
//					throw new EntityFieldException("Email " + userDto.getEmail() + " already exists","email");
//				}
//				if (!userService.isUserNicknamePresent(userDto.getNickname())
//						&& !(editedUser.getNickname().equalsIgnoreCase(userDto.getNickname()))) {
//					editedUser.setNickname(userDto.getNickname());
//
//				} else {
//					throw new EntityFieldException("Nickname " + userDto.getNickname() + " already exists","nickname");
//				}
//				
//				editedUser.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
//
//				return ResponseEntity.ok().build();
//			} else {
//				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//			}
//		}
//
//		return ResponseEntity.notFound().build();
//	}
	
//	@PostMapping(value = "/users/photo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//	public ResponseEntity<Void> addPhoto(@RequestParam("file") MultipartFile multipartFile) throws IOException {
//		UserPhoto userPhoto = UserPhoto.builder()
//				.file(multipartFile.getBytes())
//				.filename(multipartFile.getOriginalFilename())
//				.contentType(multipartFile.getContentType())
//				.createdAt(LocalDate.now())
//				.fileLength(multipartFile.getSize())
//				.build();
//		userPhotoService.saveUserPhoto(userPhoto);
//		
//		URI uri = ServletUriComponentsBuilder
//				.fromCurrentRequestUri()
//				.path("/" + userPhoto.getId().toString())
//				.build()
//				.toUri();
//		return ResponseEntity.created(uri).build();
//	}

}
