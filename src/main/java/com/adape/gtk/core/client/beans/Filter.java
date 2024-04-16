package com.adape.gtk.core.client.beans;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Filter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5759133270039093598L;
	
	@NotNull
	private List<String> showParameters;
	
	@JsonProperty("groupFilter")
	@NotNull
	private GroupFilter groupFilter;
	
	@JsonProperty("sort")
	@NotNull
	private List<Sorting> sorting;
	@NotNull
	private Page page;
	
	
	

}
