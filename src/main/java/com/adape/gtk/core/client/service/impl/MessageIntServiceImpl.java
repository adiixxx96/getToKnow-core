package com.adape.gtk.core.client.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.adape.gtk.core.client.beans.MessageDTO;
import com.adape.gtk.core.client.service.MessageIntService;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.ResponseMessage;
import com.adape.gtk.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
public class MessageIntServiceImpl implements MessageIntService{

	@Autowired @Qualifier("restTemplateOAuth") RestTemplate clientRest;
	private static final String CALLING = Constants.CALLING;
	private static final String ENTITY_TYPE = "Message";
	@Value("#{environment.webserviceGTKHost}")
	private String host;
	@Value("${MessageCreate.url:#{'/message/createMessage'}}")
	private String urlCreate;
	@Value("${MessageEdit.url:#{'/message/editMessage'}}/")
	private String urlUpdate;
	@Value("${MessageDelete.url:#{'/message/deleteMessages'}}")
	private String urlDelete;
	@Value("${MessageGet.url:#{'/message/getMessage'}}/")
	private String urlGet;
	@Value("${MessageGetFilter.url:#{'/message/getMessages'}}")
	private String urlGetFilter;
	

	@Override
	public ResponseMessage create(MessageDTO Dto, int userId) {
	    ResponseMessage responseEntity = new ResponseMessage();
	    String url = String.format("%s%s", host, urlCreate);
	    log.trace(CALLING, url);
	    final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set(Constants.HEADER_USER_ID, String.valueOf(userId));
	    headers.set(Constants.HEADER_ENTITY_TYPE, ENTITY_TYPE);
	    headers.set(Constants.HEADER_ENTITY_ACTION, "Create");
	    try {
	      ResponseEntity<MessageDTO> response = clientRest.exchange(url, HttpMethod.POST, new HttpEntity<MessageDTO>(Dto, headers), MessageDTO.class);
	      responseEntity.setStatus(HttpStatus.OK);
	      responseEntity.setMessage(response.getBody());
	      log.info("ClientRestResponse: {}", responseEntity);
	    } catch (RestClientResponseException ex) {
	      responseEntity.setStatus(ex.getRawStatusCode());
	      responseEntity.setMessage(ex.getResponseBodyAsString());
	      log.error("ClientRestError: {}", ex);
	    }
	    return responseEntity;
	}

	@Override
	public ResponseMessage edit(MessageDTO Dto, int userId) {
	    ResponseMessage responseEntity = new ResponseMessage();
	    String url = String.format("%s%s", host, urlUpdate);
	    log.trace(CALLING, url);
	    final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set(Constants.HEADER_USER_ID, String.valueOf(userId));
	    headers.set(Constants.HEADER_ENTITY_TYPE, ENTITY_TYPE);
	    headers.set(Constants.HEADER_ENTITY_ACTION, "Update");
	    try {
	      ResponseEntity<MessageDTO> response = clientRest.exchange(url, HttpMethod.PUT, new HttpEntity<MessageDTO>(Dto, headers), MessageDTO.class);
	      responseEntity.setStatus(HttpStatus.OK);
	      responseEntity.setMessage(response.getBody());
	      log.info("ClientRestResponse: {}", responseEntity);
	    } catch (RestClientResponseException ex) {
	      responseEntity.setStatus(ex.getRawStatusCode());
	      responseEntity.setMessage(ex.getResponseBodyAsString());
	      log.error("ClientRestError: {}", ex);
	    }
	    return responseEntity;
	}

	@Override
	public ResponseMessage delete(List<Integer> id, int userId) {
		// TODO Auto-generated method stub
		
		 ResponseMessage responseEntity = new ResponseMessage();
		    String url = String.format("%s%s", host, urlDelete);
		    log.trace(CALLING, url);
		    final HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.set(Constants.HEADER_USER_ID, String.valueOf(userId));
		    headers.set(Constants.HEADER_ENTITY_TYPE, ENTITY_TYPE);
		    headers.set(Constants.HEADER_ENTITY_ACTION, "Delete");
		    try {
		      ResponseEntity<String> response = clientRest.exchange(url, HttpMethod.DELETE, new HttpEntity<List<Integer>>(id, headers), String.class);
		      responseEntity.setStatus(HttpStatus.OK);
		      responseEntity.setMessage(response.getBody());
		      log.info("ClientRestResponse: {}", responseEntity);
		    } catch (RestClientResponseException ex) {
		      responseEntity.setStatus(ex.getRawStatusCode());
		      responseEntity.setMessage(ex.getResponseBodyAsString());
		      log.error("ClientRestError: {}", ex);
		    }
		    return responseEntity;
		
	}

	
	@Override
	public ResponseMessage get(Integer id, int userId) {
	    ResponseMessage responseEntity = new ResponseMessage();
	    String url = String.format("%s%s%s", host, urlGet, id);
	    log.trace(CALLING, url);
	    try {
	      ResponseEntity<MessageDTO> response = clientRest.exchange(url, HttpMethod.GET, null, MessageDTO.class);
	      responseEntity.setStatus(HttpStatus.OK);
	      responseEntity.setMessage(response.getBody());
	      log.info("ClientRestResponse: {}", responseEntity);
	    } catch (RestClientResponseException ex) {
	      responseEntity.setStatus(ex.getRawStatusCode());
	      responseEntity.setMessage(ex.getResponseBodyAsString());
	      log.error("ClientRestError: {}", ex);
	    }
	    return responseEntity;
	  }

	@Override
	public ResponseMessage get(Filter filter, int userId) {
		
		ResponseMessage responseEntity = new ResponseMessage();
	      String url = String.format("%s%s", host, urlGetFilter);
	      log.trace(CALLING, url);
	      try {
	        ResponseEntity<Response<MessageDTO>> response = clientRest.exchange(url, HttpMethod.POST, new HttpEntity<Filter>(filter),
					new ParameterizedTypeReference<Response<MessageDTO>>() {});
	        responseEntity.setStatus(response.getStatusCodeValue());
	        if(response.getStatusCode().equals(HttpStatus.OK)) {
		        responseEntity.setMessage(response.getBody());
		        log.info("ClientRestResponse: {}", responseEntity);
	        } else {
	        	 responseEntity.setMessage(new ArrayList<>());
	        	 log.info("ClientRestResponse No OK: {}", response);
	        }
	       
	      } catch (RestClientResponseException ex) {
	        responseEntity.setStatus(ex.getRawStatusCode());
	        responseEntity.setMessage(ex.getResponseBodyAsString());
	        log.error("ClientRestError: {}", ex);
	      }
		
	      return responseEntity;
	}

}