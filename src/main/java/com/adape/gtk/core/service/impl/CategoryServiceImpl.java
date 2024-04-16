package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adape.gtk.core.client.beans.CategoryDTO;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.dao.CategoryDao;
import com.adape.gtk.core.dao.EventDao;
import com.adape.gtk.core.dao.entity.Category;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.service.CategoryService;
import com.adape.gtk.core.service.EventService;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private EventDao eventDao;
	
	@Autowired
	@Lazy
	private EventService eventService;
	
	@Override
	public ResponseEntity<?> create(CategoryDTO categoryDto) {
		try {			
			Category category = parseCategory(categoryDto);
			Category newCategory= categoryDao.create(category);
			CategoryDTO newCategoryDto = parseCategory(newCategory, Utils.buildTree(List.of("")).children);
			
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Category", newCategoryDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(categoryDto.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newCategoryDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Category",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
	}
	
	@Override
	public ResponseEntity<?> edit(Integer id, CategoryDTO categoryDto) {
		try {
			Category category= parseCategory(categoryDto);
			if (id == category.getId()) {
				
				if (categoryDao.existsById(id)) {
					try {	
						Category newCategory = categoryDao.edit(category);
						categoryDto = parseCategory(newCategory, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Category", categoryDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(category.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(categoryDto);
					
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Category",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Category", "Category with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> ids) {
		try {
			List<Category> list = ids.stream().map(e-> Category.builder().id(e).build()).collect(Collectors.toList());
			List<Integer> deletedIds = categoryDao.delete(list);
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Category", deletedIds.toString());
			log.info(msgOk);
	
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Category",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception e) {
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(e)));
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public ResponseEntity<?> get(Integer id) {
		Category category = categoryDao.get(id);
		if (category != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Category", category.toString()));
			CategoryDTO categoryDto = parseCategory(category, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(categoryDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Category", ""));
			return ResponseEntity.noContent().build();
		}
	}
	
	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Category> categories = new Response<Category>();
		try {
			categories = categoryDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Category",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (categories.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Category", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Category", "size: " + categories.getResults().size()));
		
		List<CategoryDTO> categoryDTOList = new ArrayList<CategoryDTO>();
		categories.getResults().forEach(category -> {
			CategoryDTO c = parseCategory(category, Utils.buildTree(showParams).children);
			if (c != null) {
				categoryDTOList.add(c);
			}
		});
		Response<CategoryDTO> response = new Response<CategoryDTO>(categories.getSize(), categoryDTOList, categories.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}
	
	@Override
	public Category parseCategory(CategoryDTO categoryDTO) {
		if (categoryDTO == null) return null;
		try {	
			List<EventDTO> eventsDTO = categoryDTO.getEvents();
			
			categoryDTO.setEvents(null);
			
			List<Event> eventsList = new ArrayList<Event>();
			
			Category category = CustomMapper.map(categoryDTO, Category.class);
			Category oldCategory = categoryDao.get(category.getId());
		
			//Events
			if(eventsDTO != null) {
				for(EventDTO eventDTO: eventsDTO ) {
					Event event = null;
					if (eventDTO.getId() != null)
						event = eventDao.get(eventDTO.getId());
					if(event != null) {
						eventsList.add(event);
					}
				}	
			} else if (oldCategory != null){
				eventsList = oldCategory.getEvents();
			}		
			
			//Set parameters of CertificateReason relations.
			category.setEvents(eventsList);
				
			return category;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}
	
	@Override
	public CategoryDTO parseCategory(Category category, List<TreeNode<String>> params) {
		if (category == null)
			return null;
				
		Category entity = (Category) Utils.copy(category);
		
		List<EventDTO> eventsDTO = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;

			if(base.equals("events") || base.equals("all")) {
				if (category.getEvents() != null) {			
					eventsDTO = new ArrayList<EventDTO>();
					for (Event e : category.getEvents()) {
						eventsDTO.add(eventService.parseEvent(e, frags));
					}
				}
			}
		}
		entity.setEvents(null);
		
		// Map base entity
		CategoryDTO categoryDTO = CustomMapper.map(entity, CategoryDTO.class);
		
		categoryDTO.setEvents(eventsDTO);
		
		return categoryDTO;
	}
	
}