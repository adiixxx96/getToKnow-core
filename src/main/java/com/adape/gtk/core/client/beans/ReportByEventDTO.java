package com.adape.gtk.core.client.beans;

import java.io.Serializable;

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
public class ReportByEventDTO implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -1872955958790836706L;

	private Integer id;

	private UserDTO reporter;
	
	private EventDTO event;
	
	private LiteralDTO literal;
	
}
