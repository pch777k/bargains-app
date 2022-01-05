package com.pch777.bargains.validation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileSizeValidator.class})
public @interface ImageFileSize {
    String message() default "The image file is too large";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
