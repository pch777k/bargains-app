package com.pch777.bargains.utility;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.pch777.bargains.model.Category;

@Component
public class StringToEnumConverter implements Converter<String, Category> {
    @Override
    public Category convert(String source) {
        try {
            return Category.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}