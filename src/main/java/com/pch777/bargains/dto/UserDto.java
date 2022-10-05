package com.pch777.bargains.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
@ComparePasswords(passwordField = "password", confirmPasswordField = "confirmPassword")
public class UserDto {
	
    @NotBlank(message = "Nickname must not be blank")
	private String nickname;
	
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email is not valid", 
    regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
	private String email;
    
    @Size(min = 3, message = "Password should be at least 3 characters")
	private String password;
    
	private String confirmPassword;

}
