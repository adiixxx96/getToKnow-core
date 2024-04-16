package com.adape.gtk.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.LiteralDTO;
import com.adape.gtk.core.client.beans.ReportByEventDTO;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.dao.EventDao;
import com.adape.gtk.core.dao.LiteralDao;
import com.adape.gtk.core.dao.ReportByEventDao;
import com.adape.gtk.core.dao.UserDao;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.Literal;
import com.adape.gtk.core.dao.entity.ReportByEvent;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.service.EventService;
import com.adape.gtk.core.service.LiteralService;
import com.adape.gtk.core.service.ReportByEventService;
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
public class ReportByEventServiceImpl implements ReportByEventService{

	@Autowired
	private ReportByEventDao reportByEventDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EventDao eventDao;

	@Autowired
	private LiteralDao literalDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	@Lazy
	private LiteralService literalService;
	
	@Override
	public ResponseEntity<?> create(ReportByEventDTO reportByEventDto) {
		try {
			
			ReportByEvent reportByEvent = parseReportByEvent(reportByEventDto);
			ReportByEvent newReportByEvent = reportByEventDao.create(reportByEvent);
			
			ReportByEventDTO newReportByEventDto = parseReportByEvent(newReportByEvent, Utils.buildTree(List.of("")).children);
	
			log.info(String.format(Constants.ENTITY_CREATE_SUCCESSFULLY, "ReportByEvent", newReportByEventDto.toString()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, 
			  String.valueOf(newReportByEvent.getId()));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Create");
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(responseHeaders)
					.body(newReportByEventDto);
		
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_CREATE_ERROR, "ReportByEvent",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(String.format(Constants.UNEXPECTED_ERROR,Utils.printStackTraceToLog(ex)));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> edit(Integer id, ReportByEventDTO reportByEventDto) {
		try {	
			ReportByEvent reportByEvent = parseReportByEvent(reportByEventDto);
			if (id == reportByEvent.getId()) {
				
				if (reportByEventDao.existsById(id)) {
					try {
						
						ReportByEvent newReportByEvent = reportByEventDao.edit(reportByEvent);
	
						reportByEventDto = parseReportByEvent(newReportByEvent, Utils.buildTree(List.of("")).children);
						log.info(String.format(Constants.ENTITY_EDIT_SUCCESSFULLY, "ReportByEvent", reportByEventDto.toString()));
						HttpHeaders responseHeaders = new HttpHeaders();
						responseHeaders.set(Constants.HEADER_ENTITY_ID, String.valueOf(reportByEventDto.getId()));
						responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Update");
						return ResponseEntity.status(HttpStatus.OK)
						  .headers(responseHeaders)
							.body(reportByEventDto);
					}catch (CustomException e) {
						log.error(String.format(Constants.ENTITY_EDIT_ERROR, "ReportByEvent",Utils.printStackTraceToLog(e)));
						return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
					}
				} else {
					log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "ReportByEvent", "ReportByEvent with id: " + id));
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
	public ResponseEntity<?> delete(List<Integer> reportByEventDtos) {
		try {
			String msgOk = String.format(Constants.ENTITY_DELETE_SUCCESSFULLY, "ReportByEvent", reportByEventDtos.toString());
			List<ReportByEvent> reportsByEvent = new ArrayList<ReportByEvent>();
			for(Integer r : reportByEventDtos) {
				reportsByEvent.add(ReportByEvent.builder().id(r).build());
			}
			List<Integer> deletedIds = reportByEventDao.delete(reportsByEvent);
			log.info(msgOk);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set(Constants.HEADER_ENTITY_ID, deletedIds.stream().map(String::valueOf).collect(Collectors.joining(Constants.ENTITY_BREAK)));
			responseHeaders.set(Constants.HEADER_ENTITY_ACTION, "Delete");
			return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(msgOk);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_DELETE_ERROR, "ReportByEvent",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
	}

	@Override
	public ResponseEntity<?> get(Integer id) {
		ReportByEvent reportByEvent = reportByEventDao.get(id);
		if (reportByEvent != null) {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "ReportByEvent", reportByEvent.toString()));
			ReportByEventDTO reportByEventDto = parseReportByEvent(reportByEvent, Utils.buildTree(List.of("all")).children);
			return ResponseEntity.ok(reportByEventDto);
		} else {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "ReportByEvent", ""));
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> get(Filter filter) {

		List<String> showParams = filter.getShowParameters();
		Response<ReportByEvent> reportsByEvent = new Response<ReportByEvent>();
		try {
			reportsByEvent = reportByEventDao.get(filter);
		} catch (CustomException e) {
			log.error(String.format(Constants.ENTITY_GET_ERROR, "ReportByEvent",Utils.printStackTraceToLog(e)));
			return ResponseEntity.status(e.getCode()).body(e.getMsg().toString());
		}
		if (reportsByEvent.getSize() == 0) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "ReportByEvent", filter.toString()));
			return ResponseEntity.noContent().build();
		}
		log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "ReportByEvent", "size: " + reportsByEvent.getResults().size()));
		List<ReportByEventDTO> reportByEventDTOList = new ArrayList<ReportByEventDTO>();
		reportsByEvent.getResults().forEach(reportByEvent -> {
			ReportByEventDTO act = parseReportByEvent(reportByEvent, Utils.buildTree(showParams).children);
			if (act != null) {
				reportByEventDTOList.add(act);
			}
		});
		Response<ReportByEventDTO> response = new Response<ReportByEventDTO>(reportsByEvent.getSize(), reportByEventDTOList, reportsByEvent.getPage());
		log.info(String.format(Constants.RESPONSE_OK, response.toString()));
		return ResponseEntity.ok(response);
	}

	@Override
	public ReportByEventDTO parseReportByEvent(ReportByEvent reportByEvent, List<TreeNode<String>> params) {
		if (reportByEvent == null) return null;
		
		ReportByEventDTO reportByEventDTO = null;
		ReportByEvent entity = (ReportByEvent) Utils.copy(reportByEvent);
		// Process relationship entities to DTO
		UserDTO reporter = null;
		EventDTO event = null;
		LiteralDTO literal = null;
		
		for (TreeNode<String> treeNode : params) {
			String base = treeNode.data;
			List<TreeNode<String>> frags = treeNode.children;
			if(base.equals("reporter") || base.equals("all")) {
				reporter = userService.parseUser(reportByEvent.getReporter(), frags);
			}
			if(base.equals("event") || base.equals("all")) {
				event = eventService.parseEvent(reportByEvent.getEvent(), frags);
			}
			if(base.equals("literal") || base.equals("all")) {
				literal = literalService.parseLiteral(reportByEvent.getLiteral(), frags);
			}
		}
		
		// Remove base relationship entities
		entity.setReporter(null);		
		entity.setEvent(null);
		entity.setLiteral(null);
		
		// Map base entity
		reportByEventDTO = CustomMapper.map(entity, ReportByEventDTO.class);
		
		// Add parsed relationship entities to DTO 
		reportByEventDTO.setReporter(reporter);
		reportByEventDTO.setEvent(event);
		reportByEventDTO.setLiteral(literal);
		
		return reportByEventDTO;
	}

	@Override
	public ReportByEvent parseReportByEvent(ReportByEventDTO reportByEventDTO) {
		if (reportByEventDTO == null)
			return null;
		try {

			UserDTO reporterDTO = reportByEventDTO.getReporter();
			EventDTO eventDTO = reportByEventDTO.getEvent();
			LiteralDTO literalDTO = reportByEventDTO.getLiteral();

			reportByEventDTO.setReporter(null);
			reportByEventDTO.setEvent(null);
			reportByEventDTO.setLiteral(null);
			
			User reporter = null;
			Event event = null;
			Literal literal = null;
			
			ReportByEvent reportByEvent = CustomMapper.map(reportByEventDTO, ReportByEvent.class);
			ReportByEvent oldReportByEvent = reportByEventDao.get(reportByEvent.getId());
			
			// User blocked
			if(reporterDTO != null) {
				if(reporterDTO.getId() != null) {
					reporter = userDao.get(reporterDTO.getId());	
				}
			} else if (oldReportByEvent != null){
				reporter = oldReportByEvent.getReporter();
			}
			
			// User reporter
			if(eventDTO != null) {
				if(eventDTO.getId() != null) {
					event = eventDao.get(eventDTO.getId());	
				}
			} else if (oldReportByEvent != null){
				event = oldReportByEvent.getEvent();
			}
			
			// Literal
			if(literalDTO != null) {
				if(literalDTO.getId() != null) {
					literal = literalDao.get(literalDTO.getId());	
				}
			} else if (oldReportByEvent != null){
				literal = oldReportByEvent.getLiteral();
			}
			
			//Set parameters of personCost relations.
			reportByEvent.setReporter(reporter);
			reportByEvent.setEvent(event);
			reportByEvent.setLiteral(literal);
			
			return reportByEvent;
			
		}catch (Exception e) {
			log.error(Utils.printStackTraceToLog(e));
			return null;
		}
	}

}
