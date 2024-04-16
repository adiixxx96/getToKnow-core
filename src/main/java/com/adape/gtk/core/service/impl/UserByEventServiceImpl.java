package com.adape.gtk.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.UserByEventDTO;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.EventDao;

import com.adape.gtk.core.dao.UserByEventDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.dao.entity.UserByEvent;
import com.adape.gtk.core.dao.entity.UserByEvent.UserByEventId;
import com.adape.gtk.core.service.EventService;
import com.adape.gtk.core.service.UserByEventService;
import com.adape.gtk.core.service.UserService;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserByEventServiceImpl implements UserByEventService{
	
	@Autowired
	private UserByEventDao userByEventDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EventDao eventDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EventService eventService;

	@Override
	public UserByEventDTO parseUserByEvent(UserByEvent userByEvent, List<TreeNode<String>> params) {
		if (userByEvent == null)
			return null;
		
		UserByEventDTO userByEventDTO = null;
		UserByEvent entity = (UserByEvent) Utils.copy(userByEvent);
		
		// Process relationship entities to DTO
		UserDTO user = null;
		EventDTO event = null;
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("user") || base.equals("all")) {
				user = userService.parseUser(userByEvent.getUser(), frags);
			}
			if(base.equals("event") || base.equals("all")) {
				event = eventService.parseEvent(userByEvent.getEvent(), frags);
			}
		}
		
		// Remove base relationship entities
		entity.setUser(null);		
		entity.setEvent(null);		
		
		// Map base entity
		userByEventDTO = CustomMapper.map(entity, UserByEventDTO.class);
		
		// Add parsed relationship entities to DTO 
		userByEventDTO.setUser(user);
		userByEventDTO.setEvent(event);
		
		return userByEventDTO;
	}

	@Override
	public UserByEvent parseUserByEvent(UserByEventDTO userByEventDTO) {
		if (userByEventDTO == null)
			return null;
		try {
			
			UserDTO userDTO = userByEventDTO.getUser();
			EventDTO eventDTO = userByEventDTO.getEvent();

			userByEventDTO.setUser(null);
			userByEventDTO.setEvent(null);

			User user = new User();
			Event event = new Event();
			
			UserByEvent userByEvent = CustomMapper.map(userByEventDTO, UserByEvent.class);
			UserByEvent oldUserByEvent = null;
			try {
				oldUserByEvent = userByEventDao.get(new UserByEventId(userDTO.getId(), eventDTO.getId()));
			} catch (Exception e) {
				log.error(Utils.printStackTraceToLog(e));
			}
			
			//User
			if(userDTO != null) {
				if(userDTO.getId() != null) {
					user = userDao.get(userDTO.getId());
				}
			} else if (oldUserByEvent != null){
				user = oldUserByEvent.getUser();
			}		
			
			//Event
			if(eventDTO != null) {
				if(eventDTO.getId() != null) {
					event = eventDao.get(eventDTO.getId());
				}
			} else if (oldUserByEvent != null){
				event = oldUserByEvent.getEvent();
			}		
			
			//Set parameters of category relations.
			userByEvent.setUser(user);
			userByEvent.setEvent(event);
			
			//Set id
			userByEvent.setId(new UserByEventId(user.getId(), event.getId()));
			
			return userByEvent;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
