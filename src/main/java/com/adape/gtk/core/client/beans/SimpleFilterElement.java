package com.adape.gtk.core.client.beans;

import java.io.Serializable;

import com.adape.gtk.core.client.beans.FilterElements.FilterType;
import com.adape.gtk.core.client.beans.FilterElements.OperatorType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleFilterElement implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6485325931810623495L;

	private String key;
	
	private Object value;
	
	private FilterType type;
	
	private OperatorType operator;
	
}
