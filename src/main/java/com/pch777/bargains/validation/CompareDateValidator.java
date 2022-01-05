package com.pch777.bargains.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

public class CompareDateValidator implements ConstraintValidator<CompareDate, Object> {

	private String startBargainField;
	private String endBargainField;

	@Override
	public void initialize(CompareDate constraint) {
		startBargainField = constraint.starBargainField();
		endBargainField = constraint.endBargainField();
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			Object baseFieldValue = BeanUtils.getProperty(value, startBargainField);
            Object matchFieldValue = BeanUtils.getProperty(value, endBargainField);
            
			if(baseFieldValue == null || matchFieldValue == null) {
				return true;
			}
            
			final LocalDate startBargain = LocalDate.parse(baseFieldValue.toString(), formatter);
			final LocalDate endBargain = LocalDate.parse(matchFieldValue.toString(), formatter);         
			
			boolean isValid = startBargain.isBefore(endBargain) || startBargain.equals(endBargain);

			if (!isValid) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
						.addPropertyNode(endBargainField).addConstraintViolation();
			}

			return isValid;
		} catch (Exception e) {
			System.out.println("Something went wrong during compare startBargain date to endBargain date.");
		}
		return true;
	}

}
