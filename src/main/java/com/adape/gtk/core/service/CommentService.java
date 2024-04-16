package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.CommentDTO;
import com.adape.gtk.core.dao.entity.Comment;
import com.adape.gtk.core.utils.TreeNode;

public interface CommentService extends CRUDService<CommentDTO, Integer>{
	
	Comment parseComment(CommentDTO comment);
	
	CommentDTO parseComment(Comment comment, List<TreeNode<String>> params);

}