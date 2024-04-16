package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.MessageDTO;
import com.adape.gtk.core.dao.entity.Message;
import com.adape.gtk.core.utils.TreeNode;

public interface MessageService extends CRUDService<MessageDTO, Integer>{
	
	Message parseMessage(MessageDTO message);
	
	MessageDTO parseMessage(Message message, List<TreeNode<String>> params);

}