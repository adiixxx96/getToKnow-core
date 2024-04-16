package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adape.gtk.core.dao.entity.Literal;

@Repository
public interface LiteralRepository extends CrudRepository<Literal, Integer> {
	
}