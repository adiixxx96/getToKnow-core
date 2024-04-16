package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adape.gtk.core.client.beans.BlockByUserDTO;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.DeregistrationByUserDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.LiteralDTO;
import com.adape.gtk.core.client.beans.ReportByEventDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.dao.BlockByUserDao;
import com.adape.gtk.core.dao.DeregistrationByUserDao;
import com.adape.gtk.core.dao.LiteralDao;
import com.adape.gtk.core.dao.ReportByEventDao;
import com.adape.gtk.core.dao.entity.BlockByUser;
import com.adape.gtk.core.dao.entity.DeregistrationByUser;
import com.adape.gtk.core.dao.entity.Literal;
import com.adape.gtk.core.dao.entity.ReportByEvent;
import com.adape.gtk.core.service.BlockByUserService;
import com.adape.gtk.core.service.DeregistrationByUserService;
import com.adape.gtk.core.service.LiteralService;
import com.adape.gtk.core.service.ReportByEventService;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.CustomMapper;
import com.adape.gtk.core.utils.TreeNode;
import com.adape.gtk.core.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LiteralServiceImpl implements LiteralService{

	@Autowired private LiteralDao literalDao;
	@Autowired private BlockByUserDao blockByUserDao;
	@Autowired private BlockByUserService blockByUserService;
	@Autowired private DeregistrationByUserDao deregistrationByUserDao;
	@Autowired private DeregistrationByUserService deregistrationByUserService;
	@Autowired private ReportByEventDao reportByEventDao;
	@Autowired private ReportByEventService reportByEventService;
	

	@Override
	public ResponseEntity<?> create(LiteralDTO literalDto) {
		try {
			Literal literal = parseLiteral(literalDto);
			Literal newLiteral= literalDao.create(literal);
			LiteralDTO newLiteralDto = parseLiteral(newLiteral, Utils.buildTree(List.of("")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "Literal", newLiteralDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newLiteralDto.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newLiteralDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "Literal",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@Override
	public ResponseEntity<?> edit(Integer id, LiteralDTO literalDto) {
		try {
			Literal literal = parseLiteral(literalDto);
			if (id == literal.getId()) {
				
				if (literalDao.existsById(id)) {
					try {
						
						Literal newLiteral = literalDao.edit(literal);
						literalDto = parseLiteral(newLiteral, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "Literal", literalDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(literalDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(literalDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "Literal",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Literal", "Literal with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> id) {
		try {
			
			List<Literal> list = id.stream().map(e-> Literal.builder().id(e).build()).collect(Collectors.toList());
			List<Integer> deletedIds = literalDao.delete(list);
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "Literal", deletedIds.toString());
			log.info(msgOk);
	
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "Literal",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception e) {
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(e)));
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		Literal literal = literalDao.get(id);
		if (literal != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Literal", literal.toString()));
			LiteralDTO literalDto = parseLiteral(literal, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(literalDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Literal", ""));
			return ResponseEntity.noContent().build();
		}
	}
	
	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<Literal> literals = new Response<Literal>();
		try {
			literals = literalDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "Literal",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (literals.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Literal", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Literal", "size: " + literals.getResults().size()));
		List<LiteralDTO> literalDTOList = new ArrayList<LiteralDTO>();
		literals.getResults().forEach(literal -> {
			LiteralDTO act = parseLiteral(literal, Utils.buildTree(showParams).children);
			if (act != null) {
				literalDTOList.add(act);
			}
		});
		Response<LiteralDTO> response = new Response<LiteralDTO>(literals.getSize(), literalDTOList, literals.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}
	@Override
	public Literal parseLiteral(LiteralDTO literalDTO) {
		if (literalDTO == null) return null;
		try {	
			List<BlockByUserDTO> blocksDTO = literalDTO.getBlocks();
			List<DeregistrationByUserDTO> deregistrationsDTO = literalDTO.getDeregistrations();
			List<ReportByEventDTO> reportsDTO = literalDTO.getReports();
			
			literalDTO.setBlocks(null);
			literalDTO.setDeregistrations(null);
			literalDTO.setReports(null);
			
			List<BlockByUser> blocksList = new ArrayList<BlockByUser>();
			List<DeregistrationByUser> deregistrationsList = new ArrayList<DeregistrationByUser>();
			List<ReportByEvent> reportsList = new ArrayList<ReportByEvent>();
			
			Literal literal = CustomMapper.map(literalDTO, Literal.class);
			Literal oldLiteral = literalDao.get(literal.getId());
		
			//Blocks
			if(blocksDTO != null) {
				for(BlockByUserDTO blockDTO: blocksDTO ) {
					BlockByUser block = null;
					if (blockDTO.getId() != null)
						block = blockByUserDao.get(blockDTO.getId());
					if(block != null) {
						blocksList.add(block);
					}
				}	
			} else if (oldLiteral != null){
				blocksList = oldLiteral.getBlocks();
			}
			
			//Deregistrations
			if(deregistrationsDTO != null) {
				for(DeregistrationByUserDTO deregistrationDTO: deregistrationsDTO ) {
					DeregistrationByUser deregistration = null;
					if (deregistrationDTO.getId() != null)
						deregistration = deregistrationByUserDao.get(deregistrationDTO.getId());
					if(deregistration != null) {
						deregistrationsList.add(deregistration);
					}
				}	
			} else if (oldLiteral != null){
				deregistrationsList = oldLiteral.getDeregistrations();
			}
			
			//Reports
			if(reportsDTO != null) {
				for(ReportByEventDTO reportDTO: reportsDTO ) {
					ReportByEvent report = null;
					if (reportDTO.getId() != null)
						report = reportByEventDao.get(reportDTO.getId());
					if(report != null) {
						reportsList.add(report);
					}
				}	
			} else if (oldLiteral != null){
				reportsList = oldLiteral.getReports();
			}
			
			//Set parameters of CertificateReason relations.
			literal.setBlocks(blocksList);
			literal.setDeregistrations(deregistrationsList);
			literal.setReports(reportsList);
				
			return literal;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}
	@Override
	public LiteralDTO parseLiteral(Literal literal, List<TreeNode<String>>  params) {
		if (literal == null) return null;
		LiteralDTO entityDTO = new LiteralDTO();
		Literal entity = (Literal) Utils.copy(literal);
		
		// Process relationship entities to DTO
		
		//Blocks
		List<BlockByUserDTO> blocks = null;
		for (TreeNode<String> treeNode : params) {
			
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			
			if(base.equals("blocks") || base.equals("all")) {
				if(literal.getBlocks() != null) {
					blocks = new ArrayList<BlockByUserDTO>();
					for(BlockByUser act : literal.getBlocks()) {
						BlockByUserDTO dto = blockByUserService.parseBlockByUser(act, frags);
						blocks.add(dto);
					}
				}
			}
		}	
		//Deregistrations
		List<DeregistrationByUserDTO> deregistrations = null;
		for (TreeNode<String> treeNode : params) {
			
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			
			if(base.equals("deregistrations") || base.equals("all")) {
				if(literal.getDeregistrations() != null) {
					deregistrations = new ArrayList<DeregistrationByUserDTO>();
					for(DeregistrationByUser act : literal.getDeregistrations()) {
						DeregistrationByUserDTO dto = deregistrationByUserService.parseDeregistrationByUser(act, frags);
						deregistrations.add(dto);
					}
				}
			}
		}
		//Reports
		List<ReportByEventDTO> reports = null;
		for (TreeNode<String> treeNode : params) {
			
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			
			if(base.equals("reports") || base.equals("all")) {
				if(literal.getReports() != null) {
					reports = new ArrayList<ReportByEventDTO>();
					for(ReportByEvent act : literal.getReports()) {
						ReportByEventDTO dto = reportByEventService.parseReportByEvent(act, frags);
						reports.add(dto);
					}
				}
			}
		}
		
		// Remove base relationship entities
		entity.setBlocks(null);
		entity.setDeregistrations(null);
		entity.setReports(null);
		
		// Map base entity
		entityDTO = CustomMapper.map(entity, LiteralDTO.class);
		
		// Add parsed relationship entities to DTO 
		entityDTO.setBlocks(blocks);
		entityDTO.setDeregistrations(deregistrations);
		entityDTO.setReports(reports);
		
		return entityDTO;
	}

}
