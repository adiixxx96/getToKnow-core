package com.adape.gtk.core.client.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterElements implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4894373171707252417L;

	public enum FilterType {
		@JsonProperty("Integer")
		INTEGER,
		@JsonProperty("String")
		STRING,
		@JsonProperty("Boolean")
		BOOLEAN,
		@JsonProperty("IntegerList")
		INTEGER_LIST,
		@JsonProperty("LongList")
		LONG_LIST,
		@JsonProperty("StringList")
		STRING_LIST,
		@JsonProperty("Date")
		DATE,
		@JsonProperty("DateString")
        DATE_STRING,
		@JsonProperty("Special")
		SPECIAL,
		@JsonProperty("Year")
		YEAR,
		@JsonProperty("Long")
		LONG,
		@JsonProperty("Double")
		DOUBLE,
		@JsonProperty("Path")		
		PATH
	}
	public enum OperatorType {
		@JsonProperty("Contains")
		CONTAINS,
		@JsonProperty("StartsWith")
		STARTS_WITH,
		@JsonProperty("EndsWith")
		ENDS_WITH,
		@JsonProperty("Equals")
		EQUALS,
		@JsonProperty("NotEquals")
		NOT_EQUALS,
		@JsonProperty("lt")
		LESS_THAN,
		@JsonProperty("lte")
		LESS_THAN_EQUALS,
		@JsonProperty("gt")
		GREATER_THAN,
		@JsonProperty("gte")
		GREATER_THAN_EQUALS,
		@JsonProperty("Between")
		BETWEEN,
		@JsonProperty("In")
		IN,
		@JsonProperty("NotIn")
		NOT_IN,
		@JsonProperty("Null")
		IS_NULL,
		@JsonProperty("NotNull")
		IS_NOT_NULL,
		@JsonProperty("Match")
		MATCH, 		
		@JsonProperty("Exists")
		EXISTS,		
		@JsonProperty("NotExists")
		NOT_EXISTS,
	}
	public enum JoinType {

	    /** Inner join. */
	    INNER, 

	    /** Left outer join. */
	    LEFT, 

	    /** Right outer join. */
	    RIGHT
	}
	private String key;
	
	private Object value;
	
	private FilterType type;
	
	private OperatorType operator;
	
	private JoinType joinType;
	
	private boolean useRootJoin;
	
}
