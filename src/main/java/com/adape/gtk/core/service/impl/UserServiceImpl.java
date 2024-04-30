package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.adape.gtk.core.client.beans.BlockByUserDTO;
import com.adape.gtk.core.client.beans.ChatDTO;
import com.adape.gtk.core.client.beans.CommentDTO;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.DeregistrationByUserDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.FilterElements;
import com.adape.gtk.core.client.beans.GroupFilter;
import com.adape.gtk.core.client.beans.MessageDTO;
import com.adape.gtk.core.client.beans.NotificationDTO;
import com.adape.gtk.core.client.beans.Page;
import com.adape.gtk.core.client.beans.ReportByEventDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.Sorting;
import com.adape.gtk.core.client.beans.UserByEventDTO;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.client.beans.Sorting.Order;
import com.adape.gtk.core.dao.UserByEventDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.BlockByUser;
import com.adape.gtk.core.dao.entity.Chat;
import com.adape.gtk.core.dao.entity.Comment;
import com.adape.gtk.core.dao.entity.DeregistrationByUser;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.Message;
import com.adape.gtk.core.dao.entity.Notification;
import com.adape.gtk.core.dao.entity.ReportByEvent;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.dao.entity.UserByEvent;
import com.adape.gtk.core.dao.entity.UserByEvent.UserByEventId;
import com.adape.gtk.core.service.BlockByUserService;
import com.adape.gtk.core.service.ChatService;
import com.adape.gtk.core.service.CommentService;
import com.adape.gtk.core.service.DeregistrationByUserService;
import com.adape.gtk.core.service.MessageService;
import com.adape.gtk.core.service.NotificationService;
import com.adape.gtk.core.service.ReportByEventService;
import com.adape.gtk.core.service.UserByEventService;
import com.adape.gtk.core.service.UserService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
	
	@Autowired 
	private UserByEventDao userByEventDao;
	
	@Autowired
	@Lazy
	private NotificationService notificationService;
	
	@Autowired
	@Lazy
	private ChatService chatService;
	
	@Autowired
	@Lazy
	private MessageService messageService;
	
	@Autowired
	@Lazy
	private CommentService commentService;
	
	@Autowired
	@Lazy
	private UserByEventService userByEventService;
	
	@Autowired
	@Lazy
	private BlockByUserService blockService;
	
	@Autowired 
	@Lazy
	private ReportByEventService reportService;
	
	@Autowired 
	@Lazy
	private DeregistrationByUserService deregistrationService;
	
	@Override
	public ResponseEntity<?> create(UserDTO userDto) {
		try {
			
			User user = parseUser(userDto);
			User newUser = userDao.create(user);

			UserDTO newUserDto = parseUser(newUser, Utils.buildTree(List.of("")).children);

			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "User", newUserDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(newUserDto.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			
			return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(newUserDto);

		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "User", Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			log.error(String.format(Constants.UNEXPECTED_ERROR, Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, UserDTO userDto) {
		try {
			HttpHeaders responseHeaders = new HttpHeaders();
			
			List<UserByEventDTO> events = userDto.getEvents();
			userDto.setEvents(null);
			
			User user = parseUser(userDto);
			
			if (id == user.getId()) {
				
				UserDTO oldUser = null;
				
                if (userDao.existsById(id)) {
				
                	List<String> showParamsReturn = new ArrayList<>();
					try {
						
						User newUser = userDao.edit(user);

						// Edit relationships start
						
						// UserByEvent start
						if (events != null) {
							
							List<UserByEvent> oldEvents = newUser.getEvents();

							List<UserByEvent> incomingEvents = new ArrayList<UserByEvent>();
							for (UserByEventDTO dto : events) {
								incomingEvents.add(
										UserByEvent.builder()
												.event(Event.builder().id(dto.getEvent().getId()).build())
												.user(User.builder().id(newUser.getId()).build())
												.id(UserByEventId.builder().userId(newUser.getId())
														.eventId(dto.getEvent().getId()).build())
												.build());
							}

							// Delete
							for (UserByEvent dto : oldEvents) {
								if (incomingEvents.stream().filter(e -> e.getId().equals(dto.getId())).findFirst()
										.orElse(null) == null) {
									userByEventDao.delete(dto.getId());
								}
							}

							// Create/edit
							for (UserByEvent dto : incomingEvents) {
								UserByEvent existingEvents = oldEvents.stream().filter(e -> e.getId().equals(dto.getId()))
										.findFirst().orElse(null);
								if (existingEvents == null) {
									userByEventDao.create(dto);
								} else if (!dto.equals(existingEvents)) {
									userByEventDao.edit(dto);
								}
							}

							newUser.setEvents(incomingEvents);

						}
						// UserByEvent end

						
						// Edit relationships end
						
						userDto = parseUser(newUser, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "User", userDto.toString()));
						
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(userDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
												
						return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userDto);
					} catch (CustomException e) {
						e.printStackTrace();
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "User", Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "User", "User with id: " + id));
					return ResponseEntity.noContent().build();
				}

			} else {
				// Incorrect parameters given.
				String msg = String.format(Constants.BAD_REQUEST, "id: " + id);
				log.error(msg);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR, id));
			log.error(String.format(Constants.UNEXPECTED_ERROR, Utils.printStackTraceToLog(e)));
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> delete(List<Integer> userDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "User", userDtos.toString());
			List<User> users = new ArrayList<User>();
			for(Integer u : userDtos) {
				users.add(User.builder().id(u).build());
			}
			List<Integer> deletedIds = userDao.delete(users);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "User",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		User user = userDao.get(id);
		if (user != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "User", user.toString()));
			UserDTO userDto = parseUser(user, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(userDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Event", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<User> users = new Response<User>();
		try {
			users = userDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "User",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (users.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "User", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "User", "size: " + users.getResults().size()));
		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		users.getResults().forEach(user -> {
			UserDTO act = parseUser(user, Utils.buildTree(showParams).children);
			if (act != null) {
				userDTOList.add(act);
			}
		});
		Response<UserDTO> response = new Response<UserDTO>(users.getSize(), userDTOList, users.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public UserDTO parseUser(User user, List<TreeNode<String>> params) {
		if (user == null) return null;
		
		UserDTO userDTO = null;
		User entity = (User) Utils.copy(user);
		// Process relationship entities to DTO
		List<CommentDTO> comments = null;
		List<NotificationDTO> notifications = null;
		List<BlockByUserDTO> blocks = null;
		List<BlockByUserDTO> blockReports =  null;
		List<ReportByEventDTO> reports = null;
		List<DeregistrationByUserDTO> deregistrations = null;
		List<UserByEventDTO> userByEvent = null;
		List<ChatDTO> chats = null;
		List<ChatDTO> chatsAsUser2 = null;
		List<MessageDTO> messages = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			
			if(base.equals("comments") || base.equals("all")) {
				comments = new ArrayList<CommentDTO>();
				if (user.getComments() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("comments"));
					for (Comment c : user.getComments()) {
						CommentDTO dto = commentService.parseComment(c,newfrags);
					comments.add(dto);
					}
				}
			}
			
			if(base.equals("notifications") || base.equals("all")) {
				notifications = new ArrayList<NotificationDTO>();
				if (user.getNotifications() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("notifications"));
					for (Notification n : user.getNotifications()) {
						NotificationDTO dto = notificationService.parseNotification(n,newfrags);
					notifications.add(dto);
					}
				}
			}
			
			if(base.equals("blocks") || base.equals("all")) {
				blocks = new ArrayList<BlockByUserDTO>();
				if (user.getBlocks() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("blocks"));
					for (BlockByUser bbe : user.getBlocks()) {
						BlockByUserDTO dto = blockService.parseBlockByUser(bbe,newfrags);
					blocks.add(dto);
					}
				}
			}
			
			if(base.equals("blockReports") || base.equals("all")) {
				blockReports = new ArrayList<BlockByUserDTO>();
				if (user.getBlockReports() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("blockReports"));
					for (BlockByUser bbe : user.getBlockReports()) {
						BlockByUserDTO dto = blockService.parseBlockByUser(bbe,newfrags);
					blockReports.add(dto);
					}
				}
			}
			
			if(base.equals("reports") || base.equals("all")) {
				reports = new ArrayList<ReportByEventDTO>();
				if (user.getReports() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("reports"));
					for (ReportByEvent rbe : user.getReports()) {
						ReportByEventDTO dto = reportService.parseReportByEvent(rbe,newfrags);
						reports.add(dto);
					}
				}
			}
			
			if(base.equals("deregistrations") || base.equals("all")) {
				deregistrations = new ArrayList<DeregistrationByUserDTO>();
				if (user.getDeregistrations() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("deregistrations"));
					for (DeregistrationByUser dbu : user.getDeregistrations()) {
						DeregistrationByUserDTO dto = deregistrationService.parseDeregistrationByUser(dbu,newfrags);
						deregistrations.add(dto);
					}
				}
			}
			
			if(base.equals("userByEvent") || base.equals("all")) {
				userByEvent = new ArrayList<UserByEventDTO>();
				if (user.getEvents() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("userByEvent"));
					for (UserByEvent ube : user.getEvents()) {
						UserByEventDTO dto = userByEventService.parseUserByEvent(ube,newfrags);
					userByEvent.add(dto);
					}
				}
			}
			
			if(base.equals("chats") || base.equals("all")) {
				chats = new ArrayList<ChatDTO>();
				if (user.getChats() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("chats"));
					for (Chat c : user.getChats()) {
						ChatDTO dto = chatService.parseChat(c,newfrags);
						chats.add(dto);
					}
				}
			}
			
			if(base.equals("chats") || base.equals("all")) {
				chats = new ArrayList<ChatDTO>();
				if (user.getChats() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("chats"));
					for (Chat c : user.getChats()) {
						ChatDTO dto = chatService.parseChat(c,newfrags);
						chats.add(dto);
					}
				}
			}
			
			if(base.equals("chatsAsUser2") || base.equals("all")) {
				chatsAsUser2 = new ArrayList<ChatDTO>();
				if (user.getChatsAsUser2() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("chats"));
					for (Chat c : user.getChatsAsUser2()) {
						ChatDTO dto = chatService.parseChat(c,newfrags);
						chatsAsUser2.add(dto);
					}
				}
			}
			
			if(base.equals("messages") || base.equals("all")) {
				messages = new ArrayList<MessageDTO>();
				if (user.getMessages() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("messages"));
					for (Message m : user.getMessages()) {
						MessageDTO dto = messageService.parseMessage(m,newfrags);
						messages.add(dto);
					}
				}
			}
		}
		
		
		
		// Remove base relationship entities
		entity.setEvents(null);
		entity.setComments(null);
		entity.setNotifications(null);
		entity.setChats(null);
		entity.setChatsAsUser2(null);
		entity.setMessages(null);
		entity.setBlocks(null);
		entity.setBlockReports(null);
		entity.setReports(null);
		entity.setDeregistrations(null);
		
		// Map base entity
		userDTO = CustomMapper.map(entity, UserDTO.class);
		
		// Add parsed relationship entities to DTO 
		userDTO.setEvents(userByEvent);
		userDTO.setComments(comments);
		userDTO.setNotifications(notifications);
		userDTO.setChats(chats);
		userDTO.setChatsAsUser2(chatsAsUser2);
		userDTO.setMessages(messages);
		userDTO.setBlocks(blocks);
		userDTO.setBlockReports(blockReports);
		userDTO.setReports(reports);
		userDTO.setDeregistrations(deregistrations);
		
		return userDTO;
	}

	@Override
	public User parseUser(UserDTO userDTO) {
		if (userDTO == null)
			return null;
		try {

			List<UserByEventDTO> userByEventDTO= userDTO.getEvents();

			userDTO.setEvents(null);
			
			List<UserByEvent> userByEvent= new ArrayList<UserByEvent>();
			
			User user = CustomMapper.map(userDTO, User.class);
			User oldUser = userDao.get(user.getId());
			
			//UserByEvent
			if (userByEventDTO != null) {
				for (UserByEventDTO  ubeDTO: userByEventDTO) {
					userByEvent.add(userByEventService.parseUserByEvent(ubeDTO));
				}
			} else if (oldUser != null) {
				userByEvent = oldUser.getEvents();
			}
			
			//Set parameters of user relations.
			user.setEvents(userByEvent);
			
			return user;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}
	
	@Override
	public ResponseEntity<?> updatePasswordById(String password, Integer id) {
		return ResponseEntity.ok(userDao.updatePasswordById(password, id));
	}
	
	@Override
	public ResponseEntity<?> login(String email, String password) {
		
		//Filter to find user with matching email and password
				Filter filter = Filter.builder()
		    			.groupFilter(GroupFilter.builder()
		    					.operator(GroupFilter.Operator.AND)
		    					.filterElements(Arrays.asList(
		    							FilterElements.builder()
		    							.key("email")
		    							.value(email)
		    							.type(FilterElements.FilterType.STRING)
		    							.operator(FilterElements.OperatorType.EQUALS).build()
		    							)).build())
		    			.showParameters(List.of(""))
		    			.page(Page.builder().pageNo(0).pageSize(Integer.MAX_VALUE).build())
		    			.sorting(List.of(Sorting.builder().field("id").order(Order.DESC).build()))
		    			.build();
				
				// Call bonus 'get' service to get filter bonuses
				Response<UserDTO> userResp = new Response<UserDTO>();

				try {
					userResp = (Response<UserDTO>) get(filter).getBody();
				} catch (Exception e) {
					log.error(String.format(Constants.ENTITY_GET_ERROR, "User", Utils.printStackTraceToLog(e)));
				}

				if (userResp != null && userResp.getSize() > 0) {
					
					//Comparamos las contraseñas encriptadas
					UserDTO existingUser = userResp.getResults().get(0);
					if (existingUser.getPassword().equals(password)) {
						log.info("Passwords matching");
						return ResponseEntity.ok(userResp);
					} else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
					}
					
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				}
	}

}
