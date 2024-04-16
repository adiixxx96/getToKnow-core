package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adape.gtk.core.client.beans.BlockByUserDTO;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.LiteralDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.BlockByUserDao;
import com.adape.gtk.core.dao.LiteralDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.BlockByUser;
import com.adape.gtk.core.dao.entity.Literal;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.service.BlockByUserService;
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
public class BlockByUserServiceImpl implements BlockByUserService{

	@Autowired
	private BlockByUserDao blockByUserDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private LiteralDao literalDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	@Lazy
	private LiteralService literalService;
	
	@Override
	public ResponseEntity<?> create(BlockByUserDTO blockByUserDto) {
		try {
			
			BlockByUser blockByUser = parseBlockByUser(blockByUserDto);
			BlockByUser newBlockByUser= blockByUserDao.create(blockByUser);
			
			BlockByUserDTO newBlockByUserDto = parseBlockByUser(newBlockByUser, Utils.buildTree(List.of("")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "BlockByUser", newBlockByUserDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newBlockByUser.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newBlockByUserDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "BlockByUser",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, BlockByUserDTO blockByUserDto) {
		try {	
			BlockByUser blockByUser = parseBlockByUser(blockByUserDto);
			if (id == blockByUser.getId()) {
				
				if (blockByUserDao.existsById(id)) {
					try {
						
						BlockByUser newBlockByUser = blockByUserDao.edit(blockByUser);
	
						blockByUserDto = parseBlockByUser(newBlockByUser, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "BlockByUser", blockByUserDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(blockByUserDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(blockByUserDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "BlockByUser",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "BlockByUser", "BlockByUser with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> blockByUserDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "BlockByUser", blockByUserDtos.toString());
			List<BlockByUser> blocksByUser = new ArrayList<BlockByUser>();
			for(Integer b : blockByUserDtos) {
				blocksByUser.add(BlockByUser.builder().id(b).build());
			}
			List<Integer> deletedIds = blockByUserDao.delete(blocksByUser);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "BlockByUser",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		BlockByUser blockByUser= blockByUserDao.get(id);
		if (blockByUser != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "BlockByUser", blockByUser.toString()));
			BlockByUserDTO blockByUserDto = parseBlockByUser(blockByUser, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(blockByUserDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "BlockByUser", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<BlockByUser> blocksByUser = new Response<BlockByUser>();
		try {
			blocksByUser = blockByUserDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "BlockByUser",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (blocksByUser.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "BlockByUser", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "BlockByUser", "size: " + blocksByUser.getResults().size()));
		List<BlockByUserDTO> blockByUserDTOList = new ArrayList<BlockByUserDTO>();
		blocksByUser.getResults().forEach(blockByUser -> {
			BlockByUserDTO act = parseBlockByUser(blockByUser, Utils.buildTree(showParams).children);
			if (act != null) {
				blockByUserDTOList.add(act);
			}
		});
		Response<BlockByUserDTO> response = new Response<BlockByUserDTO>(blocksByUser.getSize(), blockByUserDTOList, blocksByUser.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public BlockByUserDTO parseBlockByUser(BlockByUser blockByUser, List<TreeNode<String>> params) {
		if (blockByUser == null) return null;
		
		BlockByUserDTO blockByUserDTO = null;
		BlockByUser entity = (BlockByUser) Utils.copy(blockByUser);
		// Process relationship entities to DTO
		UserDTO blocked = null;
		UserDTO reporter = null;
		LiteralDTO literal = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("blocked") || base.equals("all")) {
				blocked = userService.parseUser(blockByUser.getBlocked(), frags);
			}
			if(base.equals("reporter") || base.equals("all")) {
				reporter = userService.parseUser(blockByUser.getReporter(), frags);
			}
			if(base.equals("literal") || base.equals("all")) {
				literal = literalService.parseLiteral(blockByUser.getLiteral(), frags);
			}
		}
		
		// Remove base relationship entities
		entity.setBlocked(null);		
		entity.setReporter(null);
		entity.setLiteral(null);
		
		// Map base entity
		blockByUserDTO = CustomMapper.map(entity, BlockByUserDTO.class);
		
		// Add parsed relationship entities to DTO 
		blockByUserDTO.setBlocked(blocked);
		blockByUserDTO.setReporter(reporter);
		blockByUserDTO.setLiteral(literal);
		
		return blockByUserDTO;
	}

	@Override
	public BlockByUser parseBlockByUser(BlockByUserDTO blockByUserDTO) {
		if (blockByUserDTO == null)
			return null;
		try {

			UserDTO blockedDTO = blockByUserDTO.getBlocked();
			UserDTO reporterDTO = blockByUserDTO.getReporter();
			LiteralDTO literalDTO = blockByUserDTO.getLiteral();

			blockByUserDTO.setBlocked(null);
			blockByUserDTO.setReporter(null);
			blockByUserDTO.setLiteral(null);
			
			User blocked = null;
			User reporter = null;
			Literal literal = null;
			
			BlockByUser blockByUser = CustomMapper.map(blockByUserDTO, BlockByUser.class);
			BlockByUser oldBlockByUser = blockByUserDao.get(blockByUser.getId());
			
			// User blocked
			if(blockedDTO != null) {
				if(blockedDTO.getId() != null) {
					blocked = userDao.get(blockedDTO.getId());	
				}
			} else if (oldBlockByUser != null){
				blocked = oldBlockByUser.getBlocked();
			}
			
			// User reporter
			if(reporterDTO != null) {
				if(reporterDTO.getId() != null) {
					reporter = userDao.get(reporterDTO.getId());	
				}
			} else if (oldBlockByUser != null){
				reporter = oldBlockByUser.getReporter();
			}
			
			// Literal
			if(literalDTO != null) {
				if(literalDTO.getId() != null) {
					literal = literalDao.get(literalDTO.getId());	
				}
			} else if (oldBlockByUser != null){
				literal = oldBlockByUser.getLiteral();
			}
			
			//Set parameters of personCost relations.
			blockByUser.setBlocked(blocked);
			blockByUser.setReporter(reporter);
			blockByUser.setLiteral(literal);
			
			return blockByUser;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
