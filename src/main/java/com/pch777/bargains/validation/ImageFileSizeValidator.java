package com.pch777.bargains.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageFileSizeValidator implements ConstraintValidator<ImageFileSize, MultipartFile> {

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

		boolean isValid = true;

        Long fileSize = multipartFile.getSize();
        String contentType = multipartFile.getContentType();

        if (fileSize > 1000000 && !contentType.equals("application/octet-stream")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The image file is too large, maximum size is 1MB")
                   .addConstraintViolation();

            isValid = false;
        }

        return isValid;
    }

	
	

}
