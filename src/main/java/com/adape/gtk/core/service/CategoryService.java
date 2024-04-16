package com.adape.gtk.core.service;

import java.util.List;

import com.adape.gtk.core.client.beans.CategoryDTO;
import com.adape.gtk.core.dao.entity.Category;
import com.adape.gtk.core.utils.TreeNode;

public interface CategoryService extends CRUDService<CategoryDTO, Integer>{
	
	Category parseCategory(CategoryDTO category);
	
	CategoryDTO parseCategory(Category category, List<TreeNode<String>> params);

}
