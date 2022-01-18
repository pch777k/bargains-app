package com.pch777.bargains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.BargainPhoto;

@Repository
public interface BargainPhotoRepository extends JpaRepository<BargainPhoto, Long>{

}
