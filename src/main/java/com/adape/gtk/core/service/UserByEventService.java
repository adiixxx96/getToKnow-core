package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.UserByEventDTO;
import com.adape.gtk.core.dao.entity.UserByEvent;
import com.adape.gtk.core.utils.TreeNode;

public interface UserByEventService {

	UserByEventDTO parseUserByEvent(UserByEvent userByEvent, List<TreeNode<String>> params);
	
	UserByEvent parseUserByEvent(UserByEventDTO userByEvent);
}
