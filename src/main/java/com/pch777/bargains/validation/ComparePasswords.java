package com.pch777.bargains.validation;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ComparePasswordsValidator.class })
public @interface ComparePasswords {

	String message() default "The password confirmation does not match";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String passwordField();

	String confirmPasswordField();

}
