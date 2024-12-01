package com.adape.gtk.core.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.utils.TreeNode;

public interface UserService extends CRUDService<UserDTO, Integer>{
	
	User parseUser(UserDTO user);
	
	UserDTO parseUser(User user, List<TreeNode<String>> params);
	
	ResponseEntity<?> updatePasswordById(String password, Integer id);
	
	ResponseEntity<?> login(String email, String password);

}