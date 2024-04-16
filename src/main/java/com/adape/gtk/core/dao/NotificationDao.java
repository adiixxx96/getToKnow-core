package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.dao.entity.Notification;

@Component
public interface NotificationDao extends CRUDDao<Notification, Integer>{
	
}