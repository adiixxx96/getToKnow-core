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
public class MessageDTO implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 761062334706586903L;

	private Integer id;

	private String message;
	
	private ChatDTO chat;
	
	private UserDTO user;
	
	@Builder.Default
	private Boolean read = false;
	
	private Timestamp creationDate;
	
}
