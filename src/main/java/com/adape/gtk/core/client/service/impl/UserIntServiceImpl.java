package com.adape.gtk.core.client.service.impl;

import java.util.ArrayList;
import java.util.Base64;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.adape.gtk.core.client.service.UserIntService;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.ResponseMessage;
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
public class UserIntServiceImpl implements UserIntService{

	@Autowired @Qualifier("restTemplateOAuth") RestTemplate clientRest;
	private static final String CALLING = Constants.CALLING;
	private static final String ENTITY_TYPE = "User";
	@Value("#{environment.webserviceGTKHost}")
	private String host;
	@Value("${UserCreate.url:#{'/user/createUser'}}")
	private String urlCreate;
	@Value("${UserEdit.url:#{'/user/editUser'}}/")
	private String urlUpdate;
	@Value("${UserDelete.url:#{'/user/deleteUser'}}")
	private String urlDelete;
	@Value("${UserGet.url:#{'/user/getUser'}}/")
	private String urlGet;
	@Value("${UserGetFilter.url:#{'/user/getUsers'}}")
	private String urlGetFilter;
	@Value("${UserUpdatePasswordById.url:#{'/user/updatePasswordById'}}/")
	private String urlUpdatePasswordById;
	@Value("${UserLogin.url:#{'/user/login'}}/")
	private String urlLogin;
	

	@Override
	public ResponseMessage create(UserDTO Dto, int userId) {
	    ResponseMessage responseEntity = new ResponseMessage();
	    String url = String.format("%s%s", host, urlCreate);
	    log.trace(CALLING, url);
	    final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set(Constants.HEADER_USER_ID, String.valueOf(userId));
	    headers.set(Constants.HEADER_ENTITY_TYPE, ENTITY_TYPE);
	    headers.set(Constants.HEADER_ENTITY_ACTION, "Create");
	    try {
	      ResponseEntity<UserDTO> response = clientRest.exchange(url, HttpMethod.POST, new HttpEntity<UserDTO>(Dto, headers), UserDTO.class);
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
	public ResponseMessage edit(UserDTO Dto, int userId) {
	    ResponseMessage responseEntity = new ResponseMessage();
	    String url = String.format("%s%s", host, urlUpdate);
	    log.trace(CALLING, url);
	    final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set(Constants.HEADER_USER_ID, String.valueOf(userId));
	    headers.set(Constants.HEADER_ENTITY_TYPE, ENTITY_TYPE);
	    headers.set(Constants.HEADER_ENTITY_ACTION, "Update");
	    try {
	      ResponseEntity<UserDTO> response = clientRest.exchange(url, HttpMethod.PUT, new HttpEntity<UserDTO>(Dto, headers), UserDTO.class);
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
	      ResponseEntity<UserDTO> response = clientRest.exchange(url, HttpMethod.GET, null, UserDTO.class);
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
	        ResponseEntity<Response<UserDTO>> response = clientRest.exchange(url, HttpMethod.POST, new HttpEntity<Filter>(filter),
					new ParameterizedTypeReference<Response<UserDTO>>() {});
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
	
	@Override
	public ResponseMessage updatePasswordById(String password, int id) {
		ResponseMessage responseEntity = new ResponseMessage();
		String url = String.format("%s%s%s", host, urlUpdatePasswordById, String.valueOf(id));
		log.trace(CALLING, url);
		try {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("newPassword", new String(Base64.getEncoder().encode(password.getBytes())));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
			
			ResponseEntity<Integer> response = clientRest.exchange(url, HttpMethod.POST, requestEntity,
					new ParameterizedTypeReference<Integer>() {
					});
			responseEntity.setStatus(response.getStatusCodeValue());
			if (response.getStatusCode().equals(HttpStatus.OK)) {
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
	
	@Override
	public ResponseMessage login(String email, String password) {
		ResponseMessage responseEntity = new ResponseMessage();
		String url = String.format("%s%s", host, urlLogin);
		log.trace(CALLING, url);
		try {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("email", email);
			params.add("password", new String(Base64.getEncoder().encode(password.getBytes())));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
			
			ResponseEntity<Response<UserDTO>> response = clientRest.exchange(url, HttpMethod.POST, requestEntity,
					new ParameterizedTypeReference<Response<UserDTO>>() {
					});
			responseEntity.setStatus(response.getStatusCodeValue());
			if (response.getStatusCode().equals(HttpStatus.OK)) {
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
