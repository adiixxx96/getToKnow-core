package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.DeregistrationByUserDTO;
import com.adape.gtk.core.dao.entity.DeregistrationByUser;
import com.adape.gtk.core.utils.TreeNode;

public interface DeregistrationByUserService extends CRUDService<DeregistrationByUserDTO, Integer>{
	
	DeregistrationByUser parseDeregistrationByUser(DeregistrationByUserDTO deregistrationByUser);
	
	DeregistrationByUserDTO parseDeregistrationByUser(DeregistrationByUser deregistrationByUser, List<TreeNode<String>> params);

}