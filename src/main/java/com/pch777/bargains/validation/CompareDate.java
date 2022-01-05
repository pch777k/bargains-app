package com.pch777.bargains.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { CompareDateValidator.class })
public @interface CompareDate {

	String message() default "The start date of the bargain should be before the end date";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String starBargainField();

	String endBargainField();

}

