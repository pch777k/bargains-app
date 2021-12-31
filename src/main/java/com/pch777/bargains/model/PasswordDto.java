package com.pch777.bargains.model;

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
@ComparePasswords(passwordField = "newPassword", confirmPasswordField = "confirmNewPassword", message = "The password confirmation does not match")
public class PasswordDto {
	
	private String oldPassword;
    
    @Size(min = 3, message = "Password should be at least 3 characters")
	private String newPassword;
    
    @Size(min = 3, message = "Password should be at least 3 characters")
	private String confirmNewPassword;

}
