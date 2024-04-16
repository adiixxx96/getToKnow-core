package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.dao.entity.UserByEvent;
import com.adape.gtk.core.dao.entity.UserByEvent.UserByEventId;

@Component
public interface UserByEventDao extends CRUDDao<UserByEvent, UserByEventId>{
	void delete(UserByEventId id) throws CustomException;
}