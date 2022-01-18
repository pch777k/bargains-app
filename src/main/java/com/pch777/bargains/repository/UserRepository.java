package com.pch777.bargains.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByEmailIgnoreCase(String email);
	public Optional<User> findByNickname(String nickname);
	public User getUserByEmail(String email);
	public User getUserByNickname(String nickname);
	Boolean existsByNickname(String nickname);
    Boolean existsByEmail(String email);
	
}
