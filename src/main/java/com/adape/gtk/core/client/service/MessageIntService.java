package com.adape.gtk.core.client.service;

import java.util.List;

import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.MessageDTO;
import com.adape.gtk.core.client.beans.ResponseMessage;

public interface MessageIntService {
	ResponseMessage create(MessageDTO Dto, int userId);
	ResponseMessage edit(MessageDTO Dto, int userId);
	ResponseMessage get(Integer id, int userId);
	ResponseMessage get(Filter filter, int userId);
	ResponseMessage delete(List<Integer> id, int userId);
}
