package com.pch777.bargains;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.pch777.bargains.service.InitializerService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
class ApplicationStartup implements CommandLineRunner {

	private InitializerService initializerService;
	
	@Override
	public void run(String... args) throws Exception {
		
		initializerService.initPhotos();
		initializerService.initRoles();
		initializerService.initGuestUser();
		initializerService.initAdmin();
		initializerService.initShopData();
	
	}

}
