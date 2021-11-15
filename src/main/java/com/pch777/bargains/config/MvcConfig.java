package com.pch777.bargains.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pch777.bargains.utility.StringToEnumConverter;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }
}
