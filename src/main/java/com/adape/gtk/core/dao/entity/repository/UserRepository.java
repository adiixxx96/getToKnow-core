package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.adape.gtk.core.dao.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	
	@Modifying
	@Transactional
	@Query(nativeQuery=true, value="UPDATE user SET password = ?1 WHERE id = ?2")
	int updatePasswordById(String password, int userId);	
}