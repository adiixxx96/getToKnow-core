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
public class NotificationDTO implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4984427949204930129L;

	private Integer id;

	private String notification;
	
	private UserDTO user;
	
	private Timestamp creationDate;
	
	@Builder.Default
	private Boolean read = false;
	
}
