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
import com.adape.gtk.core.dao.entity.Message;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.dao.entity.Chat.ChatId;
import com.adape.gtk.core.service.ChatService;
import com.adape.gtk.core.service.MessageService;
import com.adape.gtk.core.service.UserService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
	private ChatDao chatDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private MessageDao messageDao;
	
	@Autowired
	private UserService userService;
	
	@Lazy
	@Autowired
	private MessageService messageService;
	
	@Override
	public ResponseEntity<?> create(ChatDTO chatDto) {
		try {
			Chat chat = parseChat(chatDto);
			
			Chat newChat= chatDao.create(chat);
			
			ChatDTO newChatDto = parseChat(newChat, Utils.buildTree(List.of("")).children);
			
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Chat", newChatDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(newChat.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
						
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newChatDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Chat", Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR, Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(ChatId id, ChatDTO chatDto) {
		try {
			Chat chat = parseChat(chatDto);
			if (id.equals(chat.getId())) {
				if (chatDao.existsById(id)) {
					try {
						HttpHeaders responseHeaders = new HttpHeaders();
						Chat newChat = chatDao.edit(chat);			
						chatDto = parseChat(newChat, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Chat", chatDto.toString()));
							
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(newChat.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
		
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(chatDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Chat", Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Chat", "Chat with id: " + id));
					return ResponseEntity.noContent().build();
				}
	
			} else {
				// Incorrect parameters given.
				String msg = String.format(Constants.BAD_REQUEST, "id: " +id);
				log.error(msg);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
			}
		} catch (Exception e) {
			log.error(String.format(Constants.UNEXPECTED_ERROR, Utils.printStackTraceToLog(e)));
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> delete(List<ChatId> id) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Chat", id.toString());
			List<Chat> chat = id.stream().map(e-> Chat.builder().id(e).build()).collect(Collectors.toList());;
			
			HttpHeaders responseHeaders = new HttpHeaders();
			
			List<ChatId> deletedIds = chatDao.delete(chat);
			log.info(msgOk);
			
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Chat", Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(ChatId id) {
		Chat chat = chatDao.get(id);
		try {
			if (chat != null) {
				log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Chat", chat.toString()));
				ChatDTO chatDto = parseChat(chat, Utils.buildTree(List.of("all")).children);
				return ResponseEntity.ok(chatDto);
			} else {
				log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Chat", ""));
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Chat", Utils.printStackTraceToLog(e)));
			return ResponseEntity.internalServerError().build();
		}
	}

	@Override
	public ResponseEntity<?> get(ChatId id, List<String> showParameters) {
		Chat chat = chatDao.get(id);
		try {
			if (chat != null) {
				log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Chat", chat.toString()));
				ChatDTO chatDto = parseChat(chat, Utils.buildTree(showParameters).children);
				return ResponseEntity.ok(chatDto);
			} else {
				log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Chat", ""));
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Chat", Utils.printStackTraceToLog(e)));
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Chat> chats = new Response<Chat>();
		try {
			chats =chatDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Chat", Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (chats.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Chat", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Chat", "size: " + chats.getResults().size()));
		List<ChatDTO> chatDTOList = new ArrayList<ChatDTO>();
		chats.getResults().forEach(chat -> {
			ChatDTO c = parseChat(chat, Utils.buildTree(showParams).children);
			if (c != null) {
				chatDTOList.add(c);
			}
		});
		Response<ChatDTO> response = new Response<ChatDTO>(chats.getSize(), chatDTOList, chats.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public ChatDTO parseChat(Chat chat, List<TreeNode<String>> params) {
		
		if (chat == null) return null;
		
		Chat entity = (Chat) Utils.copy(chat);
		
		// Process relationship entities to DTO	
		UserDTO user1DTO = null;
		UserDTO user2DTO = null;
		List<MessageDTO> messagesDTO = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			
			if(base.equals("user1") || base.equals("all")) {
				user1DTO = userService.parseUser(chat.getUser1(), frags);
			}
			
			if(base.equals("user2") || base.equals("all")) {
				user2DTO = userService.parseUser(chat.getUser1(), frags);
			}
			
			if(base.equals("messages") || base.equals("all")) {
				if (chat.getMessages() != null) {			
					messagesDTO = new ArrayList<MessageDTO>();
					for (Message m : chat.getMessages()) {
						messagesDTO.add(messageService.parseMessage(m, frags));
					}
				}
			}
		}		
		// Remove base relationship entities
		entity.setUser1(null);
		entity.setUser2(null);
		entity.setMessages(null);
		
		// Map base entity
		ChatDTO entityDTO = CustomMapper.map(entity, ChatDTO.class);
		
		// Add parsed relationship entities to DTO 
		entityDTO.setUser1(user1DTO);
		entityDTO.setUser2(user2DTO);
		entityDTO.setMessages(messagesDTO);
		
		return entityDTO;
	}

	@Override
	public Chat parseChat(ChatDTO chatDTO) {
		if (chatDTO == null) return null;
		try {
			User user1 = null;
			User user2 = null;
			List<Message> messagesList =new ArrayList<Message>();
			
			UserDTO user1DTO = chatDTO.getUser1();
			UserDTO user2DTO = chatDTO.getUser1();
			List<MessageDTO> messagesDTO = chatDTO.getMessages();

			chatDTO.setUser1(null);
			chatDTO.setUser2(null);
			chatDTO.setMessages(null);
			
			Chat chat = CustomMapper.map(chatDTO, Chat.class);
			Chat oldChat = null;
			try {
				oldChat = chatDao.get(new ChatId(user1DTO.getId(), user2DTO.getId()));
			} catch (Exception e) {
				log.error(Utils.printStackTraceToLog(e));
			}	
			
			if(user1DTO != null) {
				user1 = userDao.get(user1DTO.getId());
			} else if (oldChat != null){
				user1 = oldChat.getUser1();
			}
			
			if(user2DTO != null) {
				user2 = userDao.get(user2DTO.getId());
			} else if (oldChat != null){
				user2 = oldChat.getUser2();
			}
			
			if(messagesDTO != null) {
				for(MessageDTO messageDTO: messagesDTO ) {
					Message message = null;
					if (messageDTO.getId() != null)
						message = messageDao.get(messageDTO.getId());
					if(message != null) {
						messagesList.add(message);
					}
				}	
			} else if (oldChat != null){
				messagesList = oldChat.getMessages();
			}
			
			chat.setUser1(user1);
			chat.setUser2(user2);
			chat.setMessages(messagesList);
			
			//Set id
			chat.setId(new ChatId(user1.getId(), user2.getId()));
			
			return chat;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}
}
