package com.pch777.bargains.dto;

import org.springframework.web.multipart.MultipartFile;

import com.pch777.bargains.validation.FileSize;
import com.pch777.bargains.validation.ImageContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoFileDto {
	
	@ImageContentType
	@FileSize
	private MultipartFile fileImage;

}
