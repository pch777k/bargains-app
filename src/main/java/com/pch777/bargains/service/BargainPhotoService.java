package com.pch777.bargains.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pch777.bargains.model.BargainPhoto;
import com.pch777.bargains.repository.BargainPhotoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BargainPhotoService {
	
	private BargainPhotoRepository bargainPhotoRepository;

	public void saveBargainPhoto(BargainPhoto bargainPhoto) {
		bargainPhotoRepository.save(bargainPhoto);
	}
	
	public Optional<BargainPhoto> getBargainPhotoById(Long id) {
		return bargainPhotoRepository.findById(id);
	}
}
