package com.adape.gtk.core.client.service;

import java.util.List;

import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.ResponseMessage;
import com.adape.gtk.core.client.beans.LiteralDTO;

public interface LiteralIntService {
	ResponseMessage create(LiteralDTO Dto, int userId);
	ResponseMessage edit(LiteralDTO Dto, int userId);
	ResponseMessage get(Integer id, int userId);
	ResponseMessage get(Filter filter, int userId);
	ResponseMessage delete(List<Integer> id, int userId);
}
