package com.adape.gtk.core.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.adape.gtk.core.client.beans.ChatDTO;
import com.adape.gtk.core.dao.entity.Chat;
import com.adape.gtk.core.dao.entity.Chat.ChatId;
import com.adape.gtk.core.utils.TreeNode;

public interface ChatService  extends CRUDService<ChatDTO, ChatId>{

	Chat parseChat(ChatDTO bonusPersonStatus);

	ChatDTO parseChat(Chat chat, List<TreeNode<String>> params);

	ResponseEntity<?> get(ChatId id, List<String> showParameters);
	
}
