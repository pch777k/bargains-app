package com.pch777.bargains.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComparePricesValidator implements ConstraintValidator<ComparePrices, Object> {

	private String reducePriceField;
	private String normalPriceField;

	@Override
	public void initialize(ComparePrices constraint) {
		reducePriceField = constraint.reducePriceField();
		normalPriceField = constraint.normalPriceField();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			final Object reducePriceObject = BeanUtils.getProperty(value, reducePriceField);
			final Object normalPriceObject = BeanUtils.getProperty(value, normalPriceField);

			if(reducePriceObject == null || normalPriceObject == null) {
				return true;
			}
			
			boolean isValid = Double.parseDouble(reducePriceObject.toString()) < Double.parseDouble(normalPriceObject.toString());

			if (!isValid) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
						.addPropertyNode(normalPriceField).addConstraintViolation();
			}

			return isValid;
		} catch (Exception e) {
			log.error("Something went wrong.");
		}
		return true;
	}

}
