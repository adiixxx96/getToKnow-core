package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.TagByEventDTO;
import com.adape.gtk.core.client.beans.TagDTO;
import com.adape.gtk.core.dao.TagByEventDao;
import com.adape.gtk.core.dao.TagDao;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.Tag;
import com.adape.gtk.core.dao.entity.TagByEvent;
import com.adape.gtk.core.dao.entity.TagByEvent.TagByEventId;
import com.adape.gtk.core.service.EventService;
import com.adape.gtk.core.service.TagByEventService;
import com.adape.gtk.core.service.TagService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.Utils;
import com.adape.gtk.core.utils.TreeNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TagServiceImpl implements TagService{

	@Autowired
	private TagDao tagDao;

	@Autowired
	private TagByEventDao tagByEventDao;
	
	@Autowired
	@Lazy
	private TagByEventService tagByEventService;
	
	@Autowired
	private EventService eventService;
	
	@Override
	public ResponseEntity<?> create(TagDTO tagDto) {
		try {
			// Get relationships and set them null
			List<TagByEventDTO> events = tagDto.getEvents();
			tagDto.setEvents(null);
			Tag tag = parseTag(tagDto);
			Tag newTag= tagDao.create(tag);
			
			// Create relationships start
			
			// TagByEvent start
			if(events != null) {
				for (TagByEventDTO tbe : events) {
					TagByEvent tbeTmp = new TagByEvent(
							newTag,
							eventService.parseEvent(tbe.getEvent()));
					try {
						tagByEventDao.create(tbeTmp);
					} catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_CREATE_ERROR, "TagByEvent", Utils.printStackTraceToLog(e)));
					}
				}
			}
			//CategoryPerson end
			
			// Create relationships end
			
			TagDTO newTagDto = parseTag(newTag, Utils.buildTree(List.of("all")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Tag", newTagDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newTagDto.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newTagDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Tag",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public ResponseEntity<?> edit(Integer id, TagDTO tagDto) {
		try {
			// Get relationships and set them null
			List<TagByEventDTO> eventsDto = tagDto.getEvents();
			tagDto.setEvents(null);
			
			Tag tag = parseTag(tagDto);
			if (id == tag.getId()) {
				
				if (tagDao.existsById(id)) {
					try {
						
						Tag newTag = tagDao.edit(tag);
						
						// Edit relationships start
						
						// TagByEvent start
						if (eventsDto != null) {
							List<TagByEvent> oldEvents = newTag.getEvents();
							
							// Parse people DTOs to entities
							List<TagByEvent> incomingEvents = new ArrayList<TagByEvent>();
							eventsDto.forEach(e -> {
								
								TagByEvent tbeTmp = TagByEvent.builder()
										.id(new TagByEventId(newTag.getId(),e.getEvent().getId()))
										.tag(Tag.builder().id(newTag.getId()).build())
										.event(Event.builder().id(e.getEvent().getId()).build())
										.build();
										
								incomingEvents.add(tbeTmp);
							});
							
							// Delete
							for (TagByEvent tbe : oldEvents) {
								if(incomingEvents.stream().filter(e -> e.getId().equals(tbe.getId())).findFirst().orElse(null) == null) {
									tagByEventDao.delete(tbe.getId());
								}
							}
							
							// Create/edit
							for (TagByEvent tbe : incomingEvents) {
								TagByEvent existingTbe = oldEvents.stream().filter(e -> e.getId().equals(tbe.getId())).findFirst().orElse(null);
								if (existingTbe == null) {
									tagByEventDao.create(tbe);
								}else if(!tbe.equals(existingTbe)) {
									tagByEventDao.edit(tbe);
								}
							}

							// Set people to response object
							newTag.setEvents(incomingEvents);
						}
						// CategoryPerson end
						
						// Edit relationships end

						tagDto = parseTag(newTag, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Tag", tagDto.toString()));
							HttpHeaders responseHeaders = new HttpHeaders();
							responseHeaders.set(Constants.HEADER_ENTITY_ID, 
							  String.valueOf(tagDto.getId()));
							responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(tagDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Tag",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Tag", "Tag with id: " + id));
					return ResponseEntity.noContent().build();
				}
	
			} else {
				// Incorrect parameters given.
				String msg = String.format(Constants.BAD_REQUEST, "id: " +id);
				log.error(msg);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
			}
		} catch (Exception e) {
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(e)));
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Override
	public ResponseEntity<?> delete(List<Integer> tagIds) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Tag", tagIds.toString());
			List<Tag> tags = new ArrayList<Tag>();
			for(Integer t : tagIds) {
				tags.add(Tag.builder().id(t).build());
			}
			List<Integer> deletedIds = tagDao.delete(tags);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Tag",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}
	@Override
	public ResponseEntity<?> get(Integer id) {
		Tag tag = tagDao.get(id);
		if (tag != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Tag", tag.toString()));
			TagDTO tagDto = parseTag(tag, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(tagDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Tag", ""));
			return ResponseEntity.noContent().build();
		}
	}
	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Tag> tags = new Response<Tag>();
		try {
			tags = tagDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Tag",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (tags.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Tag", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Tag", "size: " + tags.getResults().size()));
		List<TagDTO> tagDTOList = new ArrayList<TagDTO>();
		tags.getResults().forEach(tag -> {
			TagDTO t = parseTag(tag, Utils.buildTree(showParams).children);
			if (t != null) {
				tagDTOList.add(t);
			}
		});
		Response<TagDTO> response = new Response<TagDTO>(tags.getSize(), tagDTOList, tags.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}
	@Override
	public TagDTO parseTag(Tag tag, List<TreeNode<String>> params) {
		if (tag == null)
			return null;
		TagDTO tagDTO = null;
		Tag entity = (Tag) Utils.copy(tag);
		
		// Process relationship entities to DTO
		List<TagByEventDTO> events = null;
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("events") || base.equals("all")) {
				if(tag.getEvents() != null) {
					events = new ArrayList<TagByEventDTO>();
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("event"));
					for(TagByEvent tbe : tag.getEvents()) {
						TagByEventDTO eventDto = tagByEventService.parseTagByEvent(tbe, newfrags);
						events.add(eventDto);
					}
				}
			}
		}
		
		// Remove base relationship entities
		entity.setEvents(null);
		
		// Map base entity
		tagDTO = CustomMapper.map(entity, TagDTO.class);
		
		// Add parsed relationship entities to DTO 
		tagDTO.setEvents(events);
		
		return tagDTO;
	}
	@Override
	public Tag parseTag(TagDTO tagDTO) {
		if (tagDTO == null)
			return null;
		try {
			
			List<TagByEventDTO> eventsDto = tagDTO.getEvents();

			tagDTO.setEvents(null);
			
			List<TagByEvent> events = new ArrayList<TagByEvent>();
			
			Tag tag = CustomMapper.map(tagDTO, Tag.class);
			Tag oldTag = tagDao.get(tag.getId());
			
			//CategoryPersons
			if(eventsDto != null) {
				for(TagByEventDTO tagByEventDTO: eventsDto ) {
					events.add(tagByEventService.parseTagByEvent(tagByEventDTO));
				}	
			} else if (oldTag != null){
				events = oldTag.getEvents();
			}		
			
			//Set parameters of Category relations.
			tag.setEvents(events);
			
			return tag;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
