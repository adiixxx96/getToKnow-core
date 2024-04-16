package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.TagDTO;
import com.adape.gtk.core.dao.entity.Tag;
import com.adape.gtk.core.utils.TreeNode;

public interface TagService extends CRUDService<TagDTO, Integer>{
	
	Tag parseTag(TagDTO tag);
	
	TagDTO parseTag(Tag tag, List<TreeNode<String>> params);

}
