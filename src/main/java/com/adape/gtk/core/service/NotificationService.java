package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.NotificationDTO;
import com.adape.gtk.core.dao.entity.Notification;
import com.adape.gtk.core.utils.TreeNode;

public interface NotificationService extends CRUDService<NotificationDTO, Integer>{
	
	Notification parseNotification(NotificationDTO notification);
	
	NotificationDTO parseNotification(Notification notification, List<TreeNode<String>> params);

}