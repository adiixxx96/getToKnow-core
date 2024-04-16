package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.dao.entity.TagByEvent;
import com.adape.gtk.core.dao.entity.TagByEvent.TagByEventId;

@Component
public interface TagByEventDao extends CRUDDao<TagByEvent, TagByEventId>{
	void delete(TagByEventId id) throws CustomException;
}