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
import com.pch777.bargains.model.PasswordDto;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.UserDto;
import com.pch777.bargains.model.UserPhoto;
import com.pch777.bargains.security.UserSecurity;
import com.pch777.bargains.service.UserPhotoService;
import com.pch777.bargains.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserRestController {

	private UserService userService;
	private UserSecurity userSecurity;
	private UserPhotoService userPhotoService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/users")
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

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		return userService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Transactional
	@PostMapping("/users")
	public ResponseEntity<String> addUser(@Valid @RequestBody UserDto userDto) throws EntityFieldException {
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
	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PutMapping("users/{id}")
	public ResponseEntity<Void> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id, Principal principal) throws EntityFieldException {
		if (userService.existsById(id)) {
			User editedUser = userService.findUserById(id);
			
			if (userSecurity.isOwnerOrAdmin(editedUser.getEmail(), principal.getName())) {
				if (!userService.isUserPresent(userDto.getEmail())
						&& !(editedUser.getEmail().equalsIgnoreCase(userDto.getEmail()))) {
					editedUser.setEmail(userDto.getEmail());

				} else {
					throw new EntityFieldException("Email " + userDto.getEmail() + " already exists","email");
				}
				if (!userService.isUserNicknamePresent(userDto.getNickname())
						&& !(editedUser.getNickname().equalsIgnoreCase(userDto.getNickname()))) {
					editedUser.setNickname(userDto.getNickname());

				} else {
					throw new EntityFieldException("Nickname " + userDto.getNickname() + " already exists","nickname");
				}
				
				editedUser.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		return ResponseEntity.notFound().build();
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PutMapping("users/{id}/password")
	public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordDto passwordDto, @PathVariable Long id,
			Principal principal) throws EntityFieldException {

		if (userService.existsById(id)) {
			User editedUser = userService.findUserById(id);
			if (userSecurity.isOwnerOrAdmin(editedUser.getEmail(), principal.getName())) {
				if (!bCryptPasswordEncoder.matches(passwordDto.getOldPassword(), editedUser.getPassword())) {
					throw new EntityFieldException("The old password is not correct","oldPassword");
				} else {
					editedUser.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
					return ResponseEntity.ok().build();
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		return ResponseEntity.notFound().build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("users/{id}")
	public ResponseEntity<Object> deleteUserById(@PathVariable Long id, Principal principal) {

		return userService.findById(id).map(u -> {
			if (userSecurity.isAdmin(principal.getName())) {
				userService.deleteById(id);
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}).orElse(ResponseEntity.notFound().build());

	}
	
	@Transactional
	@PostMapping("/users/{id}/photo")
	public ResponseEntity<Void> addPhotoToUser(@PathVariable Long id, @RequestParam("file") MultipartFile multipartFile) throws IOException {
		
		User user = userService.findUserById(id);
		
		UserPhoto userPhoto = UserPhoto.builder()
				.file(multipartFile.getBytes())
				.filename(multipartFile.getOriginalFilename())
				.contentType(multipartFile.getContentType())
				.createdAt(LocalDate.now())
				.fileLength(multipartFile.getSize())
				.build();
		userPhotoService.saveUserPhoto(userPhoto);
		
		user.setUserPhotoId(userPhoto.getId());
		return ResponseEntity.accepted().build();
	}
	
	
	@GetMapping("/users/photo/{id}")
	public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
		return userPhotoService.getUserPhotoById(id)
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
