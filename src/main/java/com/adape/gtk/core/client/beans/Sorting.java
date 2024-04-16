package com.adape.gtk.core.client.beans;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@Builder
public class Sorting implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3208430148970093298L;
	
	public enum Order {ASC, DESC}
	@NotNull
	private String field;
	@NotNull
	private Order order;
	
	
}
