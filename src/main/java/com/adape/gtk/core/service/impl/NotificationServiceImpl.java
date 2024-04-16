package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.NotificationDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.NotificationDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.Notification;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.service.NotificationService;
import com.adape.gtk.core.service.UserService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService{

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserService userService;
	
	@Override
	public ResponseEntity<?> create(NotificationDTO notificationDto) {
		try {
			
			Notification notification = parseNotification(notificationDto);
			Notification newNotification = notificationDao.create(notification);
			
			NotificationDTO newNotificationDto = parseNotification(newNotification, Utils.buildTree(List.of("")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Notification", newNotificationDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newNotificationDto.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newNotificationDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Notification",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, NotificationDTO notificationDto) {
		try {	
			Notification notification = parseNotification(notificationDto);
			if (id == notification.getId()) {
				
				if (notificationDao.existsById(id)) {
					try {
						
						Notification newNotification = notificationDao.edit(notification);
	
						notificationDto = parseNotification(newNotification, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Notification",notificationDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(notificationDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(notificationDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Notification",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Notification", "Notification with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> notificationDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Notification", notificationDtos.toString());
			List<Notification> notifications = new ArrayList<Notification>();
			for(Integer n : notificationDtos) {
				notifications.add(Notification.builder().id(n).build());
			}
			List<Integer> deletedIds = notificationDao.delete(notifications);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Notification",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		Notification notification = notificationDao.get(id);
		if (notification != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Notification", notification.toString()));
			NotificationDTO notificationDto = parseNotification(notification, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(notificationDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Notification", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Notification> notifications = new Response<Notification>();
		try {
			notifications = notificationDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Notification",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (notifications.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Notification", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Notification", "size: " + notifications.getResults().size()));
		List<NotificationDTO> notificationDTOList = new ArrayList<NotificationDTO>();
		notifications.getResults().forEach(notification -> {
			NotificationDTO nDto = parseNotification(notification, Utils.buildTree(showParams).children);
			if (nDto != null) {
				notificationDTOList.add(nDto);
			}
		});
		Response<NotificationDTO> response = new Response<NotificationDTO>(notifications.getSize(), notificationDTOList, notifications.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public NotificationDTO parseNotification(Notification notification, List<TreeNode<String>> params) {
		if (notification == null) return null;
		
		NotificationDTO notificationDTO = null;
		Notification entity = (Notification) Utils.copy(notification);
		
		// Process relationship entities to DTO
		UserDTO user = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("user") || base.equals("all")) {
				user = userService.parseUser(notification.getUser(), frags);
			}
		}		
		// Remove base relationship entities
		entity.setUser(null);		
		
		// Map base entity
		notificationDTO = CustomMapper.map(entity, NotificationDTO.class);
		
		// Add parsed relationship entities to DTO 
		notificationDTO.setUser(user);	
		
		return notificationDTO;
	}

	@Override
	public Notification parseNotification(NotificationDTO notificationDTO) {
		if (notificationDTO == null)
			return null;
		try {

			UserDTO userDTO = notificationDTO.getUser();
			notificationDTO.setUser(null);
			User user = null;
			
			Notification notification = CustomMapper.map(notificationDTO, Notification.class);
			Notification oldNotification = notificationDao.get(notification.getId());
			
			// User
			if(userDTO != null) {
				if(userDTO.getId() != null) {
					user = userDao.get(userDTO.getId());
				}
			} else if (oldNotification != null){
				user = oldNotification.getUser();
			}	
			
			//Set parameters of personCost relations.
			notification.setUser(user);
			
			return notification;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
