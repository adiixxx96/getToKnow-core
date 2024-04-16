package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.dao.entity.Chat;
import com.adape.gtk.core.dao.entity.Chat.ChatId;

@Component
public interface ChatDao extends CRUDDao<Chat, ChatId>{
	void delete(ChatId id) throws CustomException;
}