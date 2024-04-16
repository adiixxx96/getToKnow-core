package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.BlockByUserDTO;
import com.adape.gtk.core.dao.entity.BlockByUser;
import com.adape.gtk.core.utils.TreeNode;

public interface BlockByUserService extends CRUDService<BlockByUserDTO, Integer>{
	
	BlockByUser parseBlockByUser(BlockByUserDTO blockByUser);
	
	BlockByUserDTO parseBlockByUser(BlockByUser blockByUser, List<TreeNode<String>> params);

}