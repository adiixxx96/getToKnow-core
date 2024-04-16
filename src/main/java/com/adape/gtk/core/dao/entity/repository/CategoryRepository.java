package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adape.gtk.core.dao.entity.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
	
}