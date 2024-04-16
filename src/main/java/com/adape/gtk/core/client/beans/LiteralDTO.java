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
public class LiteralDTO implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6151809409932377351L;

	private Integer id;

	private String literal;
	
	private Integer type;
	
	@Builder.Default
	private Boolean active = true;
	
	private List<BlockByUserDTO> blocks;
	
	private List<DeregistrationByUserDTO> deregistrations;
	
	private List <ReportByEventDTO> reports;
	
}
