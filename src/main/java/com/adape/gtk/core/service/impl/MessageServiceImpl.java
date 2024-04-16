package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adape.gtk.core.client.beans.ChatDTO;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.MessageDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.ChatDao;
import com.adape.gtk.core.dao.MessageDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.Chat;
import com.adape.gtk.core.dao.entity.Chat.ChatId;
import com.adape.gtk.core.dao.entity.Chat.ChatId.ChatIdBuilder;
import com.adape.gtk.core.dao.entity.Message;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.service.ChatService;
import com.adape.gtk.core.service.MessageService;
import com.adape.gtk.core.service.UserService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService{

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private UserService userService;
	
	@Override
	public ResponseEntity<?> create(MessageDTO messageDto) {
		try {
			
			Message message = parseMessage(messageDto);
			Message newMessage= messageDao.create(message);
			
			MessageDTO newMessageDto = parseMessage(newMessage, Utils.buildTree(List.of("")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Message", newMessageDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newMessage.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newMessageDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Message",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, MessageDTO messageDto) {
		try {	
			Message message = parseMessage(messageDto);
			if (id == message.getId()) {
				
				if (messageDao.existsById(id)) {
					try {
						
						Message newMessage = messageDao.edit(message);
	
						messageDto = parseMessage(newMessage, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Message", messageDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(messageDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(messageDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Message",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Message", "Message with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> messageDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Message", messageDtos.toString());
			List<Message> messages = new ArrayList<Message>();
			for(Integer m : messageDtos) {
				messages.add(Message.builder().id(m).build());
			}
			List<Integer> deletedIds = messageDao.delete(messages);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Message",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		Message message = messageDao.get(id);
		if (message != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Message", message.toString()));
			MessageDTO messageDto = parseMessage(message, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(messageDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Message", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Message> messages = new Response<Message>();
		try {
			messages = messageDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Message",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (messages.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Message", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Message", "size: " + messages.getResults().size()));
		List<MessageDTO> messageDTOList = new ArrayList<MessageDTO>();
		messages.getResults().forEach(message -> {
			MessageDTO act = parseMessage(message, Utils.buildTree(showParams).children);
			if (act != null) {
				messageDTOList.add(act);
			}
		});
		Response<MessageDTO> response = new Response<MessageDTO>(messages.getSize(), messageDTOList, messages.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public MessageDTO parseMessage(Message message, List<TreeNode<String>> params) {
		if (message == null) return null;
		
		MessageDTO messageDTO = null;
		Message entity = (Message) Utils.copy(message);
		// Process relationship entities to DTO
		ChatDTO chat = null;
		UserDTO user = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("chat") || base.equals("all")) {
				chat = chatService.parseChat(message.getChat(), frags);
			}
			if(base.equals("user") || base.equals("all")) {
				user = userService.parseUser(message.getUser(), frags);
			}
		}
		
		// Remove base relationship entities
		entity.setChat(null);		
		entity.setUser(null);	
		
		// Map base entity
		messageDTO = CustomMapper.map(entity, MessageDTO.class);
		
		// Add parsed relationship entities to DTO 
		messageDTO.setChat(chat);
		messageDTO.setUser(user);	
		
		return messageDTO;
	}

	@Override
	public Message parseMessage(MessageDTO messageDTO) {
		if (messageDTO == null)
			return null;
		try {

			ChatDTO chatDTO = messageDTO.getChat();
			UserDTO userDTO = messageDTO.getUser();

			messageDTO.setChat(null);
			messageDTO.setUser(null);
			Chat chat = null;
			User user = null;
			
			Message message = CustomMapper.map(messageDTO, Message.class);
			Message oldMessage = messageDao.get(message.getId());
			
			// Chat
			if(chatDTO != null) {
				ChatIdBuilder id = ChatId.builder();
				id.user1Id(chatDTO.getUser1().getId());
				id.user2Id(chatDTO.getUser1().getId());
				chat = chatDao.get(id.build());
				
			} else if (oldMessage != null){
				chat = oldMessage.getChat();
			}
			
			// User
			if(userDTO != null) {
				if(userDTO.getId() != null) {
					user = userDao.get(userDTO.getId());	
				}
			} else if (oldMessage != null){
				user = oldMessage.getUser();
			}
			
			//Set parameters of personCost relations.
			message.setChat(chat);
			message.setUser(user);
			
			return message;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
