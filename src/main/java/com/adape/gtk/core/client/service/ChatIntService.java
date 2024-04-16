package com.adape.gtk.core.client.service;

import java.util.List;

import com.adape.gtk.core.client.beans.ChatDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.ResponseMessage;

public interface ChatIntService {
	ResponseMessage create(ChatDTO Dto, int userId);
	ResponseMessage edit(ChatDTO Dto, int userId);
	ResponseMessage get(Integer id, int userId);
	ResponseMessage get(Filter filter, int userId);
	ResponseMessage delete(List<ChatDTO> id, int userId);
}
