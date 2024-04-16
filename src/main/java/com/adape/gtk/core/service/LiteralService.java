package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.LiteralDTO;
import com.adape.gtk.core.dao.entity.Literal;
import com.adape.gtk.core.utils.TreeNode;

public interface LiteralService extends CRUDService<LiteralDTO, Integer>{
	
	Literal parseLiteral(LiteralDTO literal);
	
	LiteralDTO parseLiteral(Literal literal, List<TreeNode<String>> params);

}