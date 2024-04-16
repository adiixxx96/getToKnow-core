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

import com.adape.gtk.core.client.beans.CategoryDTO;
import com.adape.gtk.core.client.beans.CommentDTO;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.DeregistrationByUserDTO;
import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.ReportByEventDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.TagByEventDTO;
import com.adape.gtk.core.client.beans.UserByEventDTO;
import com.adape.gtk.core.dao.EventDao;
import com.adape.gtk.core.dao.TagByEventDao;
import com.adape.gtk.core.dao.UserByEventDao;
import com.adape.gtk.core.dao.entity.Comment;
import com.adape.gtk.core.dao.entity.DeregistrationByUser;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.ReportByEvent;
import com.adape.gtk.core.dao.entity.Tag;
import com.adape.gtk.core.dao.entity.TagByEvent;
import com.adape.gtk.core.dao.entity.TagByEvent.TagByEventId;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.dao.entity.UserByEvent;
import com.adape.gtk.core.dao.entity.UserByEvent.UserByEventId;
import com.adape.gtk.core.service.CategoryService;
import com.adape.gtk.core.service.CommentService;
import com.adape.gtk.core.service.DeregistrationByUserService;
import com.adape.gtk.core.service.EventService;
import com.adape.gtk.core.service.ReportByEventService;
import com.adape.gtk.core.service.TagByEventService;
import com.adape.gtk.core.service.UserByEventService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventServiceImpl implements EventService{

	@Autowired
	private EventDao eventDao;

	@Autowired
	private TagByEventDao tagByEventDao;
	
	@Autowired 
	private UserByEventDao userByEventDao;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	@Lazy
	private TagByEventService tagByEventService;
	
	@Autowired
	@Lazy
	private CommentService commentService;
	
	@Autowired 
	@Lazy
	private UserByEventService userByEventService;
	
	@Autowired
	@Lazy
	private ReportByEventService reportService;
	
	@Autowired 
	private DeregistrationByUserService deregistrationService;
	
	@Override
	public ResponseEntity<?> create(EventDTO eventDto) {
		try {
			List<TagByEventDTO> tags = eventDto.getTags();
			eventDto.setTags(null);
			List<UserByEventDTO> users = eventDto.getUsers();
			eventDto.setUsers(null);

			Event event = parseEvent(eventDto);
			Event newEvent = eventDao.create(event);

			// Create relationships start
			
			//TagByEvent start
			ArrayList<TagByEvent> newTags = null;
			if (tags != null) {
				newTags = new ArrayList<TagByEvent>();
				for (TagByEventDTO dto : tags) {
					try {
						if (dto.getTag() != null)
							newTags.add(tagByEventDao.create(TagByEvent.builder()
									.tag(Tag.builder().id(dto.getTag().getId()).build())
									.event(Event.builder().id(newEvent.getId()).build())
									.id(TagByEventId.builder().eventId(newEvent.getId())
											.tagId(dto.getTag().getId())
											.build())
									.build()));
					} catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_CREATE_ERROR, "TagByEvent",
								Utils.printStackTraceToLog(e)));
					}
				}
			}
			newEvent.setTags(newTags);
			//TagByEvent end
			
			//UserByEvent start
			ArrayList<UserByEvent> newUsers = null;
			if (users != null) {
				newUsers = new ArrayList<UserByEvent>();
				for (UserByEventDTO dto : users) {
					try {
						if (dto.getUser() != null)
							newUsers.add(userByEventDao.create(UserByEvent.builder()
									.user(User.builder().id(dto.getUser().getId()).build())
									.event(Event.builder().id(newEvent.getId()).build())
									.owner(dto.getOwner())
									.participant(dto.getParticipant())
									.registrationDate(dto.getRegistrationDate())
									.id(UserByEventId.builder().eventId(newEvent.getId())
											.userId(dto.getUser().getId())
											.build())
									.build()));
					} catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_CREATE_ERROR, "UserByEvent",
								Utils.printStackTraceToLog(e)));
					}
				}
			}
			newEvent.setUsers(newUsers);
			//TagByEvent end
			
			// Create relationships end

			EventDTO newEventDto = parseEvent(newEvent, Utils.buildTree(List.of("")).children);

			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Event", newEventDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(newEventDto.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			
			return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(newEventDto);

		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Event", Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			log.error(String.format(Constants.UNEXPECTED_ERROR, Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, EventDTO eventDto) {
		try {
			HttpHeaders responseHeaders = new HttpHeaders();
			
			List<TagByEventDTO> tags = eventDto.getTags();
			eventDto.setTags(null);
			List<UserByEventDTO> users = eventDto.getUsers();
			eventDto.setUsers(null);
			
			Event event = parseEvent(eventDto);
			
			if (id == event.getId()) {
				
				EventDTO oldEvent = null;
				
                if (eventDao.existsById(id)) {
				
                	List<String> showParamsReturn = new ArrayList<>();
					try {
						
						Event newEvent = eventDao.edit(event);

						// Edit relationships start

						// TagByEvent start
						if (tags != null) {
														
							List<TagByEvent> oldTags = newEvent.getTags();

							List<TagByEvent> incomingTags = new ArrayList<TagByEvent>();
							for (TagByEventDTO dto : tags) {
								incomingTags.add(
										TagByEvent.builder()
												.event(Event.builder().id(newEvent.getId()).build())
												.tag(Tag.builder().id(dto.getTag().getId()).build())
												.id(TagByEventId.builder().eventId(newEvent.getId())
														.tagId(dto.getTag().getId()).build())
												.build());
							}

							// Delete
							for (TagByEvent dto : oldTags) {
								if (incomingTags.stream().filter(t -> t.getId().equals(dto.getId())).findFirst()
										.orElse(null) == null) {
									tagByEventDao.delete(dto.getId());
								}
							}

							// Create/edit
							for (TagByEvent dto : incomingTags) {
								TagByEvent existingTags = oldTags.stream().filter(t -> t.getId().equals(dto.getId()))
										.findFirst().orElse(null);
								if (existingTags == null) {
									tagByEventDao.create(dto);
								} else if (!dto.equals(existingTags)) {
									tagByEventDao.edit(dto);
								}
							}

							newEvent.setTags(incomingTags);

						}
						// TagByEvent end
						
						// UserByEvent start
						if (users != null) {
							
							List<UserByEvent> oldUsers = newEvent.getUsers();

							List<UserByEvent> incomingUsers = new ArrayList<UserByEvent>();
							for (UserByEventDTO dto : users) {
								incomingUsers.add(
										UserByEvent.builder()
												.event(Event.builder().id(newEvent.getId()).build())
												.user(User.builder().id(dto.getUser().getId()).build())
												.id(UserByEventId.builder().eventId(newEvent.getId())
														.userId(dto.getUser().getId()).build())
												.build());
							}

							// Delete
							for (UserByEvent dto : oldUsers) {
								if (incomingUsers.stream().filter(u -> u.getId().equals(dto.getId())).findFirst()
										.orElse(null) == null) {
									userByEventDao.delete(dto.getId());
								}
							}

							// Create/edit
							for (UserByEvent dto : incomingUsers) {
								UserByEvent existingUsers = oldUsers.stream().filter(u -> u.getId().equals(dto.getId()))
										.findFirst().orElse(null);
								if (existingUsers == null) {
									userByEventDao.create(dto);
								} else if (!dto.equals(existingUsers)) {
									userByEventDao.edit(dto);
								}
							}

							newEvent.setUsers(incomingUsers);

						}
						// UserByEvent end

						
						// Edit relationships end
						
						eventDto = parseEvent(newEvent, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Event", eventDto.toString()));
						
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(eventDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
												
						return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(eventDto);
					} catch (CustomException e) {
						e.printStackTrace();
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Event", Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Event", "Event with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> eventDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Event", eventDtos.toString());
			List<Event> events = new ArrayList<Event>();
			for(Integer e : eventDtos) {
				events.add(Event.builder().id(e).build());
			}
			List<Integer> deletedIds = eventDao.delete(events);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Event",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		Event event = eventDao.get(id);
		if (event != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Event", event.toString()));
			EventDTO eventDto = parseEvent(event, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(eventDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Event", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Event> events = new Response<Event>();
		try {
			events = eventDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Event",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (events.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Event", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Event", "size: " + events.getResults().size()));
		List<EventDTO> eventDTOList = new ArrayList<EventDTO>();
		events.getResults().forEach(event -> {
			EventDTO act = parseEvent(event, Utils.buildTree(showParams).children);
			if (act != null) {
				eventDTOList.add(act);
			}
		});
		Response<EventDTO> response = new Response<EventDTO>(events.getSize(), eventDTOList, events.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public EventDTO parseEvent(Event event, List<TreeNode<String>> params) {
		if (event == null) return null;
		
		EventDTO eventDTO = null;
		Event entity = (Event) Utils.copy(event);
		// Process relationship entities to DTO
		CategoryDTO category = null;
		List<CommentDTO> comments = null;
		List<ReportByEventDTO> reports = null;
		List<DeregistrationByUserDTO> deregistrations = null;
		List<TagByEventDTO> tagByEvent = null;
		List<UserByEventDTO> userByEvent = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			
			if(base.equals("category") || base.equals("all")) {
				category = categoryService.parseCategory(event.getCategory(), frags);
			}
			
			if(base.equals("comments") || base.equals("all")) {
				comments = new ArrayList<CommentDTO>();
				if (event.getComments() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("comments"));
					for (Comment c : event.getComments()) {
						CommentDTO dto = commentService.parseComment(c,newfrags);
					comments.add(dto);
					}
				}
			}
			
			if(base.equals("reports") || base.equals("all")) {
				reports = new ArrayList<ReportByEventDTO>();
				if (event.getReports() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("reports"));
					for (ReportByEvent rbe : event.getReports()) {
						ReportByEventDTO dto = reportService.parseReportByEvent(rbe,newfrags);
					reports.add(dto);
					}
				}
			}
			
			if(base.equals("deregistrations") || base.equals("all")) {
				deregistrations = new ArrayList<DeregistrationByUserDTO>();
				if (event.getDeregistrations() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("deregistrations"));
					for (DeregistrationByUser dbu : event.getDeregistrations()) {
						DeregistrationByUserDTO dto = deregistrationService.parseDeregistrationByUser(dbu,newfrags);
						deregistrations.add(dto);
					}
				}
			}
			
			if(base.equals("tagByEvent") || base.equals("all")) {
				tagByEvent = new ArrayList<TagByEventDTO>();
				if (event.getTags() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("tagByEvent"));
					for (TagByEvent tbe : event.getTags()) {
						TagByEventDTO dto = tagByEventService.parseTagByEvent(tbe,newfrags);
					tagByEvent.add(dto);
					}
				}
			}
			if(base.equals("userByEvent") || base.equals("all")) {
				userByEvent = new ArrayList<UserByEventDTO>();
				if (event.getUsers() != null) {
					List<TreeNode<String>> newfrags = new ArrayList<TreeNode<String>>();
					newfrags.addAll(frags);
					newfrags.add(new TreeNode<String>("userByEvent"));
					for (UserByEvent ube : event.getUsers()) {
						UserByEventDTO dto = userByEventService.parseUserByEvent(ube,newfrags);
					userByEvent.add(dto);
					}
				}
			}
		}
		
		
		
		// Remove base relationship entities
		entity.setTags(null);		
		entity.setUsers(null);	
		
		// Map base entity
		eventDTO = CustomMapper.map(entity, EventDTO.class);
		
		// Add parsed relationship entities to DTO 
		eventDTO.setTags(tagByEvent);
		eventDTO.setUsers(userByEvent);		
		
		return eventDTO;
	}

	@Override
	public Event parseEvent(EventDTO eventDTO) {
		if (eventDTO == null)
			return null;
		try {

			List<TagByEventDTO> tagByEventDTO= eventDTO.getTags();
			List<UserByEventDTO> userByEventDTO= eventDTO.getUsers();

			eventDTO.setTags(null);
			eventDTO.setUsers(null);
			
			List<TagByEvent> tagByEvent= new ArrayList<TagByEvent>();
			List<UserByEvent> userByEvent= new ArrayList<UserByEvent>();
			
			Event event = CustomMapper.map(eventDTO, Event.class);
			Event oldEvent = eventDao.get(event.getId());
			
			//TagByEvent
			if (tagByEventDTO != null) {
				for (TagByEventDTO  tbeDTO: tagByEventDTO) {
					tagByEvent.add(tagByEventService.parseTagByEvent(tbeDTO));
				}
			} else if (oldEvent != null) {
				tagByEvent = oldEvent.getTags();
			}
			
			//UserByEvent
			if (userByEventDTO != null) {
				for (UserByEventDTO  ubeDTO: userByEventDTO) {
					userByEvent.add(userByEventService.parseUserByEvent(ubeDTO));
				}
			} else if (oldEvent != null) {
				userByEvent = oldEvent.getUsers();
			}
			
			//Set parameters of personCost relations.
			event.setTags(tagByEvent);
			event.setUsers(userByEvent);
			
			return event;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
