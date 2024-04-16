package com.adape.gtk.core.dao;

import org.springframework.stereotype.Component;

import com.adape.gtk.core.dao.entity.Comment;

@Component
public interface CommentDao extends CRUDDao<Comment, Integer>{
	
}