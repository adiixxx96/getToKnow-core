package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.DeregistrationByUserDTO;
import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.LiteralDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.DeregistrationByUserDao;
import com.adape.gtk.core.dao.EventDao;
import com.adape.gtk.core.dao.LiteralDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.DeregistrationByUser;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.Literal;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.service.DeregistrationByUserService;
import com.adape.gtk.core.service.EventService;
import com.adape.gtk.core.service.LiteralService;
import com.adape.gtk.core.service.UserService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeregistrationByUserServiceImpl implements DeregistrationByUserService{

	@Autowired
	private DeregistrationByUserDao deregistrationByUserDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EventDao eventDao;

	@Autowired
	private LiteralDao literalDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	@Lazy
	private EventService eventService;
	
	@Autowired
	@Lazy
	private LiteralService literalService;
	
	@Override
	public ResponseEntity<?> create(DeregistrationByUserDTO deregistrationByUserDto) {
		try {
			
			DeregistrationByUser deregistrationByUser = parseDeregistrationByUser(deregistrationByUserDto);
			DeregistrationByUser newDeregistrationByUser= deregistrationByUserDao.create(deregistrationByUser);
			
			DeregistrationByUserDTO newDeregistrationByUserDto = parseDeregistrationByUser(newDeregistrationByUser, Utils.buildTree(List.of("")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "DeregistrationByUser", newDeregistrationByUserDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newDeregistrationByUser.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newDeregistrationByUserDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "DeregistrationByUser",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, DeregistrationByUserDTO deregistrationByUserDto) {
		try {	
			DeregistrationByUser deregistrationByUser = parseDeregistrationByUser(deregistrationByUserDto);
			if (id == deregistrationByUser.getId()) {
				
				if (deregistrationByUserDao.existsById(id)) {
					try {
						
						DeregistrationByUser newDeregistrationByUser = deregistrationByUserDao.edit(deregistrationByUser);
	
						deregistrationByUserDto = parseDeregistrationByUser(newDeregistrationByUser, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "DeregistrationByUser", deregistrationByUserDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(deregistrationByUserDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(deregistrationByUserDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "DeregistrationByUser",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "DeregistrationByUser", "DeregistrationByUser with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> deregistrationByUserDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "DeregistrationByUser", deregistrationByUserDtos.toString());
			List<DeregistrationByUser> deregistrationsByUser = new ArrayList<DeregistrationByUser>();
			for(Integer d : deregistrationByUserDtos) {
				deregistrationsByUser.add(DeregistrationByUser.builder().id(d).build());
			}
			List<Integer> deletedIds = deregistrationByUserDao.delete(deregistrationsByUser);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "DeregistrationByUser",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		DeregistrationByUser deregistrationByUser= deregistrationByUserDao.get(id);
		if (deregistrationByUser != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "DeregistrationByUser", deregistrationByUser.toString()));
			DeregistrationByUserDTO deregistrationByUserDto = parseDeregistrationByUser(deregistrationByUser, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(deregistrationByUserDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "DeregistrationByUser", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<DeregistrationByUser> deregistrationsByUser = new Response<DeregistrationByUser>();
		try {
			deregistrationsByUser = deregistrationByUserDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "DeregistrationByUser",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (deregistrationsByUser.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "DeregistrationByUser", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "DeregistrationByUser", "size: " + deregistrationsByUser.getResults().size()));
		List<DeregistrationByUserDTO> deregistrationByUserDTOList = new ArrayList<DeregistrationByUserDTO>();
		deregistrationsByUser.getResults().forEach(deregistrationByUser -> {
			DeregistrationByUserDTO act = parseDeregistrationByUser(deregistrationByUser, Utils.buildTree(showParams).children);
			if (act != null) {
				deregistrationByUserDTOList.add(act);
			}
		});
		Response<DeregistrationByUserDTO> response = new Response<DeregistrationByUserDTO>(deregistrationsByUser.getSize(), deregistrationByUserDTOList, deregistrationsByUser.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public DeregistrationByUserDTO parseDeregistrationByUser(DeregistrationByUser deregistrationByUser, List<TreeNode<String>> params) {
		if (deregistrationByUser == null) return null;
		
		DeregistrationByUserDTO deregistrationByUserDTO = null;
		DeregistrationByUser entity = (DeregistrationByUser) Utils.copy(deregistrationByUser);
		// Process relationship entities to DTO
		UserDTO user = null;
		EventDTO event = null;
		LiteralDTO literal = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("user") || base.equals("all")) {
				user = userService.parseUser(deregistrationByUser.getUser(), frags);
			}
			if(base.equals("event") || base.equals("all")) {
				event = eventService.parseEvent(deregistrationByUser.getEvent(), frags);
			}
			if(base.equals("literal") || base.equals("all")) {
				literal = literalService.parseLiteral(deregistrationByUser.getLiteral(), frags);
			}
		}
		
		// Remove base relationship entities
		entity.setUser(null);		
		entity.setEvent(null);
		entity.setLiteral(null);
		
		// Map base entity
		deregistrationByUserDTO = CustomMapper.map(entity, DeregistrationByUserDTO.class);
		
		// Add parsed relationship entities to DTO 
		deregistrationByUserDTO.setUser(user);
		deregistrationByUserDTO.setEvent(event);
		deregistrationByUserDTO.setLiteral(literal);
		
		return deregistrationByUserDTO;
	}

	@Override
	public DeregistrationByUser parseDeregistrationByUser(DeregistrationByUserDTO deregistrationByUserDTO) {
		if (deregistrationByUserDTO == null)
			return null;
		try {

			UserDTO userDTO = deregistrationByUserDTO.getUser();
			EventDTO eventDTO = deregistrationByUserDTO.getEvent();
			LiteralDTO literalDTO = deregistrationByUserDTO.getLiteral();

			deregistrationByUserDTO.setUser(null);
			deregistrationByUserDTO.setEvent(null);
			deregistrationByUserDTO.setLiteral(null);
			
			User user = null;
			Event event = null;
			Literal literal = null;
			
			DeregistrationByUser deregistrationByUser = CustomMapper.map(deregistrationByUserDTO, DeregistrationByUser.class);
			DeregistrationByUser oldDeregistrationByUser = deregistrationByUserDao.get(deregistrationByUser.getId());
			
			// User blocked
			if(userDTO != null) {
				if(userDTO.getId() != null) {
					user = userDao.get(userDTO.getId());	
				}
			} else if (oldDeregistrationByUser != null){
				user = oldDeregistrationByUser.getUser();
			}
			
			// User reporter
			if(eventDTO != null) {
				if(eventDTO.getId() != null) {
					event = eventDao.get(eventDTO.getId());	
				}
			} else if (oldDeregistrationByUser != null){
				event = oldDeregistrationByUser.getEvent();
			}
			
			// Literal
			if(literalDTO != null) {
				if(literalDTO.getId() != null) {
					literal = literalDao.get(literalDTO.getId());	
				}
			} else if (oldDeregistrationByUser != null){
				literal = oldDeregistrationByUser.getLiteral();
			}
			
			//Set parameters of personCost relations.
			deregistrationByUser.setUser(user);
			deregistrationByUser.setEvent(event);
			deregistrationByUser.setLiteral(literal);
			
			return deregistrationByUser;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
