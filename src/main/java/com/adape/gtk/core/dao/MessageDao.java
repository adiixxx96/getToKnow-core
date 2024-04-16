package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.dao.entity.Message;

@Component
public interface MessageDao extends CRUDDao<Message, Integer>{
	
}