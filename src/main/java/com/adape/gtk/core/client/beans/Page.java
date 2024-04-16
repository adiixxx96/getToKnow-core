package com.adape.gtk.core.client.beans;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Page implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7913293027954621399L;
	
	@NotNull
	private Integer pageNo;
	@NotNull
	private Integer pageSize;
	
	
}
