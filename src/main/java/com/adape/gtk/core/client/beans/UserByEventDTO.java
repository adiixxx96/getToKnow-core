package com.adape.gtk.core.client.beans;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserByEventDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1636157570094815910L;

	private UserDTO user;

	private EventDTO event;
	
	private Boolean owner;
	
	private Boolean participant;
	
	private Timestamp registrationDate;
	
	private Timestamp deregistrationDate;
	
	private Boolean deregistrationVoluntary;
	
}
