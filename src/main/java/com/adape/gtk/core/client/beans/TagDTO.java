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
public class TagDTO implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4087274481328419416L;

	private Integer id;

	private String tag;
	
	@Builder.Default
	private Boolean active = true;
	
	private List<TagByEventDTO> events;
	
}
