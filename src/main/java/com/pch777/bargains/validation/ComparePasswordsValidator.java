package com.pch777.bargains.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComparePasswordsValidator implements ConstraintValidator<ComparePasswords, Object> {

	private String passwordField;
	private String confirmPasswordField;

	@Override
	public void initialize(ComparePasswords constraint) {
		passwordField = constraint.passwordField();
		confirmPasswordField = constraint.confirmPasswordField();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			final String passwordString = BeanUtils.getProperty(value, passwordField);
			final String confirmPasswordString = BeanUtils.getProperty(value, confirmPasswordField);
			
			boolean isValid = passwordString.equals(confirmPasswordString);

			if (!isValid) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
						.addPropertyNode(confirmPasswordField).addConstraintViolation();
			}

			return isValid;
		} catch (Exception e) {
			log.error("Something went wrong.");
		}
		return true;
	}

}
