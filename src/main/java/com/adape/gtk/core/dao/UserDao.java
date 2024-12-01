package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.dao.entity.User;

@Component
public interface UserDao extends CRUDDao<User, Integer>{
	
	int updatePasswordById(String password, Integer id);
	
}