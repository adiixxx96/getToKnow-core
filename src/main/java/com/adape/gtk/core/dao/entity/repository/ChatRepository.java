package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adape.gtk.core.dao.entity.Chat;
import com.adape.gtk.core.dao.entity.Chat.ChatId;

@Repository
public interface ChatRepository extends CrudRepository<Chat, ChatId> {
	
}