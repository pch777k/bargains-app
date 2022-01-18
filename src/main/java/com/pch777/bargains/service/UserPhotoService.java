package com.pch777.bargains.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pch777.bargains.model.UserPhoto;
import com.pch777.bargains.repository.UserPhotoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserPhotoService {
	
	private UserPhotoRepository userPhotoRepository;

	public void saveUserPhoto(UserPhoto userPhoto) {
		userPhotoRepository.save(userPhoto);
	}
	
	public Optional<UserPhoto> getUserPhotoById(Long id) {
		return userPhotoRepository.findById(id);
	}
}
