package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.dao.entity.Event;

@Component
public interface EventDao extends CRUDDao<Event, Integer>{
	
}