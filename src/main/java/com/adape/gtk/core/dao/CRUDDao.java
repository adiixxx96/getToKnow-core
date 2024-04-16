package com.adape.gtk.core.dao;

import java.util.List;

import org.springframework.retry.annotation.Retryable;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.Response;

@Retryable(value = CustomException.class)
public interface CRUDDao<T, ID> {

	T create(T entity) throws CustomException;
	
	T edit(T entity) throws CustomException;

	List<ID> delete(List<T> id) throws CustomException;
	
	boolean existsById(ID id);

	T get(ID id);

	Response<T> get(Filter filter) throws CustomException;

}