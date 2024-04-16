package com.adape.gtk.core.client.service;

import java.util.List;

import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.ResponseMessage;
import com.adape.gtk.core.client.beans.DeregistrationByUserDTO;

public interface DeregistrationByUserIntService {
	ResponseMessage create(DeregistrationByUserDTO Dto, int userId);
	ResponseMessage edit(DeregistrationByUserDTO Dto, int userId);
	ResponseMessage get(Integer id, int userId);
	ResponseMessage get(Filter filter, int userId);
	ResponseMessage delete(List<Integer> id, int userId);
}
