package com.adape.gtk.core.client.beans;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3008639641725328515L;

	private long size;
	
	private List<T> results;
	
	private long page;

	@Override
	public String toString() {
		return "Response [size=" + size + ", page=" + page + "]";
	}
	
	
}
