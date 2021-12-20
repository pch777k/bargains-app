package com.pch777.bargains.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pch777.bargains.exception.ResourceNotFoundException;
import com.pch777.bargains.model.Shop;
import com.pch777.bargains.repository.ShopRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ShopService {

	private ShopRepository shopRepository;
	
	public List<Shop> getAllShops() {
		return shopRepository.findAll();
	}
	
	public Shop getShopById(Long id) throws ResourceNotFoundException {
		return shopRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found a shop with id: " + id));
	}
	
	public void addShop(Shop shop) {
		shopRepository.save(shop);
	}
	
	
}
