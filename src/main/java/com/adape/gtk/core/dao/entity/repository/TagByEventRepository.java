package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adape.gtk.core.dao.entity.TagByEvent;
import com.adape.gtk.core.dao.entity.TagByEvent.TagByEventId;

@Repository
public interface TagByEventRepository extends CrudRepository<TagByEvent, TagByEventId> {
	
}