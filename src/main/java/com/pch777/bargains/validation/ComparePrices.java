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
@Constraint(validatedBy = { ComparePricesValidator.class })
public @interface ComparePrices {

	String message() default "Normal price must be higher than reduced price";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String reducePriceField();

	String normalPriceField();

}
