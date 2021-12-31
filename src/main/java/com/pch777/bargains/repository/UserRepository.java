package com.pch777.bargains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	public User getUserByEmail(String email);
	public User getUserByNickname(String nickname);
	
}
