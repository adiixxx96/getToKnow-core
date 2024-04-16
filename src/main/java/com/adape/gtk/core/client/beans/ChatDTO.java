package com.adape.gtk.core.client.beans;

import java.io.Serializable;
import java.util.List;

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
public class ChatDTO implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2364642042421304363L;

	private UserDTO user1;

	private UserDTO user2;
	
	private Boolean status;
	
	private List<MessageDTO> messages;
	
}
