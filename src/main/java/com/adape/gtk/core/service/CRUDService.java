package com.adape.gtk.core.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.adape.gtk.core.client.beans.Filter;


public interface CRUDService<T, ID>{

	ResponseEntity<?> create(T Dto); 
	ResponseEntity<?> edit(ID id, T Dto);
	ResponseEntity<?> delete(List<ID> id);
	ResponseEntity<?> get(ID id);
	ResponseEntity<?> get(Filter filter);
}
