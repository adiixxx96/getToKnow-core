package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.utils.TreeNode;

public interface EventService extends CRUDService<EventDTO, Integer>{
	
	Event parseEvent(EventDTO event);
	
	EventDTO parseEvent(Event event, List<TreeNode<String>> params);

}