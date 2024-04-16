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
public class CommentDTO implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4908152090935323970L;

	private Integer id;

	private String comment;
	
	private Timestamp creationDate;
	
	private UserDTO user;
	
	private EventDTO event;
	
	private CommentDTO parent;
	
}
