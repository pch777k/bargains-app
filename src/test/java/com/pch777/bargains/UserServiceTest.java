package com.pch777.bargains;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import com.pch777.bargains.dto.UserDto;
import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.User;
import com.pch777.bargains.service.UserService;

@SpringBootTest
//@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {

	@Autowired
	UserService userService;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Test
	void shouldGetAllUsers() throws ResourceNotFoundException {
		// given		
		User thirdUser = userService.userDtoToUser(givenThirdUser());
		userService.registerUser(thirdUser);
		
		// when
		List<User> all = userService.getAllUsers();
	
		// then
		// guest and administrator accounts are created when the application is started
		Assertions.assertEquals(3, all.size());
	}
	
	@Test
	void shouldGetUserByEmail() throws ResourceNotFoundException {
		// given
		User thirdUser = userService.userDtoToUser(givenThirdUser());
		userService.registerUser(thirdUser);
		
		// when
		User user = userService.findUserByEmail("third@demomail.com");
	
		// then
		Assertions.assertEquals("third@demomail.com", user.getEmail());
	}
	
	@Test
	void shouldGetUserById() throws ResourceNotFoundException {
		// given
		User thirdUser = userService.userDtoToUser(givenThirdUser());
		userService.registerUser(thirdUser);
		
		// when
		User user = userService.findById(3L).orElseThrow();
	
		// then
		Assertions.assertEquals("third@demomail.com", user.getEmail());
		Assertions.assertEquals(3L, user.getId());
	}
	
	@Test
	void shouldDeleteUserById() throws ResourceNotFoundException {
		// given
		User thirdUser = userService.userDtoToUser(givenThirdUser());
		userService.registerUser(thirdUser);
		
		// when
		userService.deleteUserById(3L);
		List<User> all = userService.getAllUsers();
		Boolean isExist = userService.existsById(3L);
	
		// then
		Assertions.assertEquals(2, all.size());
		Assertions.assertFalse(isExist);
	}
	
	@Test
	void shouldExistUserWithEmail() throws ResourceNotFoundException {
		// given
		User thirdUser = userService.userDtoToUser(givenThirdUser());
		userService.registerUser(thirdUser);
		
		// when
		Boolean isPresent = userService.isUserPresent("third@demomail.com");
	
		// then
		Assertions.assertTrue(isPresent);

	}
	
	@Test
	void shouldExistUserWithId() throws ResourceNotFoundException {
		// given
		User thirdUser = userService.userDtoToUser(givenThirdUser());
		userService.registerUser(thirdUser);
		
		// when
		Boolean isExist = userService.existsById(3L);
	
		// then
		Assertions.assertTrue(isExist);
	}
	
	
	private UserDto givenThirdUser() {
		return UserDto.builder()
				.email("third@demomail.com")
				.nickname("third-user")
				.password("pass1234")
				.confirmPassword("pass1234")
				.build();
	}
	
}
