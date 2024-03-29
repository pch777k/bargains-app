package com.pch777.bargains.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pch777.bargains.dto.UserDto;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.Role;
import com.pch777.bargains.model.User;
import com.pch777.bargains.model.UserPhoto;
import com.pch777.bargains.repository.RoleRepository;
import com.pch777.bargains.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final UserPhotoService userPhotoService;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final Random random;
	
	@Transactional
	public void registerUser(User user) throws ResourceNotFoundException {
		
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		if(user.getUserPhoto() == null) {
			UserPhoto userPhoto = userPhotoService.getUserPhotoById(1L).orElseThrow(ResourceNotFoundException::new);
			user.setUserPhoto(userPhoto);
		}
		Role userRole = roleRepository.findRoleByName("USER");
	  
	    Set<Role> roles = new HashSet<>();
	    roles.add(userRole);
		user.setRoles(roles); 
		userRepository.save(user);
	}
	
	@Transactional
	public void registerAdmin(User admin) {
		admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
	
		Role userRole = roleRepository.findRoleByName("USER");
		Role adminRole = roleRepository.findRoleByName("ADMIN");
	  
	    Set<Role> roles = new HashSet<>();
	    roles.add(adminRole);
	    roles.add(userRole);
		admin.setRoles(roles); 
		userRepository.save(admin);
	}
	
	public User userDtoToUser(UserDto userDto) {
		return User.builder()
				.nickname(userDto.getNickname())
				.email(userDto.getEmail())
				.password(userDto.getPassword())
				.build();
	}
	
	public User findUserByEmail(String email) {
		
		return userRepository.getUserByEmail(email);
	}
	
//	public User findUserById(Long id) {
//		return userRepository.findById(id).get();
//	}
	
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public Page<User> getUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
	
	public boolean isUserPresent(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public boolean isUserNicknamePresent(String nickname) {
		return userRepository.existsByNickname(nickname);
	}
	
	public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
	
	public User randomUser() {
		List<User> users = userRepository.findAll();
		return users.get(random.nextInt(users.size()));
	}
	
	public boolean isEmptyUsersBargainsList(String email) {
		return userRepository.getUserByEmail(email).getBargains().isEmpty();
	}
	
	public void editUser(User user) {
		userRepository.save(user);
	}
	
	public void deleteUserById(Long id) throws ResourceNotFoundException {
		if (!existsById(id)) {
			throw new ResourceNotFoundException("Cannot find bargain with id: " + id);
		}
		userRepository.deleteById(id);		
	}
	
	public void deleteById(Long id) {
		userRepository.deleteById(id);		
	}

	
}
