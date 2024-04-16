package com.adape.gtk.core.dao.entity.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adape.gtk.core.dao.entity.ReportByEvent;

@Repository
public interface ReportByEventRepository extends CrudRepository<ReportByEvent, Integer> {
	
}