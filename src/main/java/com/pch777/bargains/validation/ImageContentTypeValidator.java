package com.pch777.bargains.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageContentTypeValidator implements ConstraintValidator<ImageContentType, MultipartFile> {

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

		boolean isValid = true;

        String contentType = multipartFile.getContentType();

        if (!isSupportedContentType(contentType) && !contentType.equals("application/octet-stream")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Only png, jpg or jpeg images are allowed")
                   .addConstraintViolation();

            isValid = false;
        }

        return isValid;
    }
	
	private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }

}
