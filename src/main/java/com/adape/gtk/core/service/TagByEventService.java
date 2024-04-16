package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.TagByEventDTO;
import com.adape.gtk.core.dao.entity.TagByEvent;
import com.adape.gtk.core.utils.TreeNode;

public interface TagByEventService {

	TagByEventDTO parseTagByEvent(TagByEvent tagByEvent, List<TreeNode<String>> params);
	
	TagByEvent parseTagByEvent(TagByEventDTO tagByEvent);
}
