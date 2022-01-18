package com.pch777.bargains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.UserPhoto;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long>{

}
