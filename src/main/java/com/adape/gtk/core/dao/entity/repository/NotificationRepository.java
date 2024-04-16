package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adape.gtk.core.dao.entity.Notification;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {
	
}