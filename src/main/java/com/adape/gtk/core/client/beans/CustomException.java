package com.adape.gtk.core.client.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@Builder
public class CustomException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6881169046467036153L;

	private Integer code;
	
	private Object msg;

}
