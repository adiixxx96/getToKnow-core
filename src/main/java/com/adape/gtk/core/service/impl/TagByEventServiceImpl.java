package com.adape.gtk.core.service.impl;

import java.util.List;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.TagByEventDTO;
import com.adape.gtk.core.client.beans.TagDTO;
import com.adape.gtk.core.dao.EventDao;
import com.adape.gtk.core.dao.TagByEventDao;
import com.adape.gtk.core.dao.TagDao;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.Tag;
import com.adape.gtk.core.dao.entity.TagByEvent;
import com.adape.gtk.core.dao.entity.TagByEvent.TagByEventId;
import com.adape.gtk.core.service.EventService;
import com.adape.gtk.core.service.TagByEventService;
import com.adape.gtk.core.service.TagService;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TagByEventServiceImpl implements TagByEventService{
	
	@Autowired
	private TagByEventDao tagByEventDao;
	
	@Autowired
	private TagDao tagDao;
	
	@Autowired
	private EventDao eventDao;
	
	@Autowired
	@Lazy
	private TagService tagService;
	
	@Autowired
	@Lazy
	private EventService eventService;

	@Override
	public TagByEventDTO parseTagByEvent(TagByEvent tagByEvent, List<TreeNode<String>> params) {
		if (tagByEvent == null)
			return null;
		
		TagByEventDTO tagByEventDTO = null;
		TagByEvent entity = (TagByEvent) Utils.copy(tagByEvent);
		
		// Process relationship entities to DTO
		TagDTO tag = null;
		EventDTO event = null;
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("tag") || base.equals("all")) {
				tag = tagService.parseTag(tagByEvent.getTag(), frags);
			}
			if(base.equals("event") || base.equals("all")) {
				event = eventService.parseEvent(tagByEvent.getEvent(), frags);
			}
		}
		
		// Remove base relationship entities
		entity.setTag(null);		
		entity.setEvent(null);		
		
		// Map base entity
		tagByEventDTO = CustomMapper.map(entity, TagByEventDTO.class);
		
		// Add parsed relationship entities to DTO 
		tagByEventDTO.setTag(tag);
		tagByEventDTO.setEvent(event);
		
		return tagByEventDTO;
	}

	@Override
	public TagByEvent parseTagByEvent(TagByEventDTO tagByEventDTO) {
		if (tagByEventDTO == null)
			return null;
		try {
			
			TagDTO tagDTO = tagByEventDTO.getTag();
			EventDTO eventDTO = tagByEventDTO.getEvent();

			tagByEventDTO.setTag(null);
			tagByEventDTO.setEvent(null);

			Tag tag = new Tag();
			Event event = new Event();
			
			TagByEvent tagByEvent = CustomMapper.map(tagByEventDTO, TagByEvent.class);
			TagByEvent oldTagByEvent = null;
			try {
				oldTagByEvent = tagByEventDao.get(new TagByEventId(tagDTO.getId(), eventDTO.getId()));
			} catch (Exception e) {
				log.error(Utils.printStackTraceToLog(e));
			}
			
			//Tag
			if(tagDTO != null) {
				if(tagDTO.getId() != null) {
					tag = tagDao.get(tagDTO.getId());
				}
			} else if (oldTagByEvent != null){
				tag = oldTagByEvent.getTag();
			}		
			
			//Event
			if(eventDTO != null) {
				if(eventDTO.getId() != null) {
					event = eventDao.get(eventDTO.getId());
				}
			} else if (oldTagByEvent != null){
				event = oldTagByEvent.getEvent();
			}		
			
			//Set parameters of category relations.
			tagByEvent.setTag(tag);
			tagByEvent.setEvent(event);
			
			//Set id
			tagByEvent.setId(new TagByEventId(tag.getId(), event.getId()));
			
			return tagByEvent;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
