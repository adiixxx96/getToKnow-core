package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adape.gtk.core.dao.entity.UserByEvent;
import com.adape.gtk.core.dao.entity.UserByEvent.UserByEventId;

@Repository
public interface UserByEventRepository extends CrudRepository<UserByEvent, UserByEventId> {
	
}