package com.pch777.bargains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pch777.bargains.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findRoleByName(String name);

}
