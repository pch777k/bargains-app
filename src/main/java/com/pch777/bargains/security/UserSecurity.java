package com.pch777.bargains.security;

import org.springframework.stereotype.Component;

import com.pch777.bargains.model.User;
import com.pch777.bargains.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserSecurity {
	
	private UserService userService;
	
	public boolean isOwnerOrAdmin(String ownerEmail, String loggedEmail) {
		return isOwner(ownerEmail, loggedEmail) || isAdmin(loggedEmail);
	}

	public boolean isOwner(String ownerEmail, String loggedEmail) {
		return loggedEmail.equalsIgnoreCase(ownerEmail);
	}

	public boolean isAdmin(String loggedEmail) {
		if(userService.isUserPresent(loggedEmail)) {
		User user = userService.findUserByEmail(loggedEmail);
		return user.getRoles()
				.stream()
				.anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
		}
		return false;
	}
}
