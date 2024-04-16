package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adape.gtk.core.client.beans.CommentDTO;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.CommentDao;
import com.adape.gtk.core.dao.EventDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.Comment;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.service.CommentService;
import com.adape.gtk.core.service.EventService;
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
public class CommentServiceImpl implements CommentService{

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EventDao eventDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	@Lazy
	private EventService eventService;
	
	@Autowired
	@Lazy
	private CommentService commentService;
	
	@Override
	public ResponseEntity<?> create(CommentDTO commentDto) {
		try {
			
			Comment comment = parseComment(commentDto);
			Comment newComment= commentDao.create(comment);
			
			CommentDTO newCommentDto = parseComment(newComment, Utils.buildTree(List.of("")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Comment", newCommentDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newComment.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newCommentDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Comment",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, CommentDTO commentDto) {
		try {	
			Comment comment = parseComment(commentDto);
			if (id == comment.getId()) {
				
				if (commentDao.existsById(id)) {
					try {
						
						Comment newComment = commentDao.edit(comment);
	
						commentDto = parseComment(newComment, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Comment", commentDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(commentDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(commentDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Comment",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Comment", "Comment with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> commentDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Comment", commentDtos.toString());
			List<Comment> comments = new ArrayList<Comment>();
			for(Integer c : commentDtos) {
				comments.add(Comment.builder().id(c).build());
			}
			List<Integer> deletedIds = commentDao.delete(comments);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Comment",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		Comment comment = commentDao.get(id);
		if (comment != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Comment", comment.toString()));
			CommentDTO commentDto = parseComment(comment, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(commentDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Comment", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Comment> comments = new Response<Comment>();
		try {
			comments = commentDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Comment",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (comments.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Comment", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Comment", "size: " + comments.getResults().size()));
		List<CommentDTO> commentDTOList = new ArrayList<CommentDTO>();
		comments.getResults().forEach(comment -> {
			CommentDTO act = parseComment(comment, Utils.buildTree(showParams).children);
			if (act != null) {
				commentDTOList.add(act);
			}
		});
		Response<CommentDTO> response = new Response<CommentDTO>(comments.getSize(), commentDTOList, comments.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public CommentDTO parseComment(Comment comment, List<TreeNode<String>> params) {
		if (comment == null) return null;
		
		CommentDTO commentDTO = null;
		Comment entity = (Comment) Utils.copy(comment);
		// Process relationship entities to DTO
		UserDTO user = null;
		EventDTO event = null;
		CommentDTO parent = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("user") || base.equals("all")) {
				user = userService.parseUser(comment.getUser(), frags);
			}
			if(base.equals("event") || base.equals("all")) {
				event = eventService.parseEvent(comment.getEvent(), frags);
			}
			if(base.equals("parent") || base.equals("all")) {
				parent = commentService.parseComment(comment.getParent(), frags);
			}
		}
		
		// Remove base relationship entities
		entity.setUser(null);		
		entity.setEvent(null);
		entity.setParent(null);
		
		// Map base entity
		commentDTO = CustomMapper.map(entity, CommentDTO.class);
		
		// Add parsed relationship entities to DTO 
		commentDTO.setUser(user);
		commentDTO.setEvent(event);
		commentDTO.setParent(parent);
		
		return commentDTO;
	}

	@Override
	public Comment parseComment(CommentDTO commentDTO) {
		if (commentDTO == null)
			return null;
		try {

			UserDTO userDTO = commentDTO.getUser();
			EventDTO eventDTO = commentDTO.getEvent();
			CommentDTO parentDTO = commentDTO.getParent();

			commentDTO.setUser(null);
			commentDTO.setEvent(null);
			commentDTO.setParent(null);
			
			User user = null;
			Event event = null;
			Comment parent = null;
			
			Comment comment = CustomMapper.map(commentDTO, Comment.class);
			Comment oldComment = commentDao.get(comment.getId());
			
			// User
			if(userDTO != null) {
				if(userDTO.getId() != null) {
					user = userDao.get(userDTO.getId());	
				}
			} else if (oldComment != null){
				user = oldComment.getUser();
			}
			
			// Event
			if(eventDTO != null) {
				if(eventDTO.getId() != null) {
					event = eventDao.get(eventDTO.getId());	
				}
			} else if (oldComment != null){
				event = oldComment.getEvent();
			}
			
			// Parent
			if(parentDTO != null) {
				if(parentDTO.getId() != null) {
					parent = commentDao.get(parentDTO.getId());	
				}
			} else if (oldComment != null){
				parent = oldComment.getParent();
			}
			
			//Set parameters of personCost relations.
			comment.setUser(user);
			comment.setEvent(event);
			comment.setParent(parent);
			
			return comment;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
