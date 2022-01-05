package com.pch777.bargains.model;

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import com.pch777.bargains.validation.ImageContentType;
import com.pch777.bargains.validation.ImageFileSize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
	
    @NotEmpty(message = "Nickname must not be empty")
	private String nickname;
	
    @NotEmpty(message = "Email must not be empty")
	private String email;
    
    @ImageContentType
    @ImageFileSize
    private MultipartFile fileImage;

}
