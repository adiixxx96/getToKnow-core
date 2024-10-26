package com.adape.gtk.core.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.dao.entity.Event;

@Component
public interface EventDao extends CRUDDao<Event, Integer>{
	
	List<Integer> getEventIdsByBody(List<String> words);
	
	List<Integer> getEventIdsByParticipantsNumber(int min, int max);
}