package com.pch777.bargains.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@OpenAPIDefinition(
        info = @Info(
        	title = "Bargains API", 
        	description = "Bargains demo project",
        	version = "v1.0.0",
        	contact = @Contact(
                 name = "pch777",
        	     url = "http://https://bargainsdemo.herokuapp.com",
        	     email = "kozdranski@protonmail.com"))
)
public class OpenApiConfiguration {

}
