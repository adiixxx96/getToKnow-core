package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.ChatDTO;
import com.adape.gtk.core.dao.entity.Chat;
import com.adape.gtk.core.utils.TreeNode;

public interface ChatService  extends CRUDService<ChatDTO, Integer>{

	Chat parseChat(ChatDTO bonusPersonStatus);

	ChatDTO parseChat(Chat chat, List<TreeNode<String>> params);
	
}
