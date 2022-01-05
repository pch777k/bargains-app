package com.pch777.bargains.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.pch777.bargains.validation.ComparePasswords;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ComparePasswords(passwordField = "password", confirmPasswordField = "confirmPassword", message = "The password confirmation does not match")
public class UserDto {
	
    @NotEmpty(message = "Nickname must not be empty")
	private String nickname;
	
    @NotEmpty(message = "Email must not be empty")
	private String email;
    
    @Size(min = 3, message = "Password should be at least 3 characters")
	private String password;
    
	private String confirmPassword;

}
