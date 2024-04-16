package com.adape.gtk.core.client.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupFilter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7945293639205223231L;

	@JsonProperty("filter")
	private List<FilterElements> filterElements;
	
	@JsonProperty("groupFilter")
	private List<GroupFilter> groupFilter;
	
	public enum Operator { AND, OR }
	
	private Operator operator;
	
	private boolean useGroupRootJoin;

}
