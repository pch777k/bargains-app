package com.pch777.bargains.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

		boolean isValid = true;

        Long fileSize = multipartFile.getSize();
        String contentType = multipartFile.getContentType();

        if (multipartFile.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Please choose a file")
                   .addConstraintViolation();
            isValid = false;
        }
        
        if (fileSize > 1000000 && !contentType.equals("application/octet-stream")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The file is too large, maximum size is 1MB")
                   .addConstraintViolation();

            isValid = false;
        }
        return isValid;
    }

	
	

}
