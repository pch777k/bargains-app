package com.pch777.bargains.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	Page<Activity> findByUserId(Pageable pageable, Long userId);
	List<Activity> findByBargainId(Long bargainId);
	List<Activity> findByUserId(Long userId);
	void deleteActivitiesByBargainId(Long bargainId);
	
}
