package com.adape.gtk.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.GroupFilter;
import com.adape.gtk.core.client.beans.Page;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.Sorting;

import com.adape.gtk.core.dao.ReportByEventDao;
import com.adape.gtk.core.dao.entity.ReportByEvent;
import com.adape.gtk.core.dao.entity.repository.ReportByEventRepository;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReportByEventDaoImpl implements ReportByEventDao{
	
	@Autowired
	private ReportByEventRepository reportByEventRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public ReportByEvent create(ReportByEvent reportByEvent) throws CustomException {
		ReportByEvent newReportByEvent = null;
		try {
			newReportByEvent = reportByEventRepository.save(reportByEvent);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		}catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newReportByEvent;
	}

	@Override
	public ReportByEvent edit(ReportByEvent reportByEvent) throws CustomException {
		ReportByEvent newReportByEvent = null;
		try {	
			newReportByEvent = reportByEventRepository.save(reportByEvent);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newReportByEvent;
	}

	@Override
	public List<Integer> delete(List<ReportByEvent> id) throws CustomException {
		List<Integer> deletedIds = new ArrayList<Integer>();
		try {
			for (ReportByEvent entity : id) {
				reportByEventRepository.delete(entity);
				deletedIds.add(entity.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(500, e);
		}
		return deletedIds;
	}

	@Override
	public boolean existsById(Integer id) {
		if (reportByEventRepository.existsById(id)) {
			log.info(String.format(Constants.ENTITY_EXIST, "ReportByEvent"));
			return true;
		} else {
			log.info(String.format(Constants.ENTITY_NOT_EXIST, "ReportByEvent"));
			return false;
		}
	}
	
	@Override
	public ReportByEvent get(Integer id) {
		Optional<ReportByEvent> optReportByEvent = reportByEventRepository.findById(id);
		if (optReportByEvent.isEmpty()) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "ReportByEvent", "id: " + id));
			return null;
		} else {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "ReportByEvent", "id: " + id));	
			return optReportByEvent.get();
		}
	}

	@Override
	public Response<ReportByEvent> get(Filter filter) throws CustomException{
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ReportByEvent> query = criteriaBuilder.createQuery(ReportByEvent.class);
		Root<ReportByEvent> root = query.from(ReportByEvent.class);
		List<Predicate> predicates = new ArrayList<>();
		GroupFilter filters = filter.getGroupFilter();
		Page page = filter.getPage();
		List<Sorting> sorting = filter.getSorting();
		List<String> errors = new ArrayList<String>();
		
		predicates = QueryUtils.generatePredicate(filters, criteriaBuilder, root, errors, query);
		
		if (sorting.size() > 0) {
			try {
				List<Order> orderList = QueryUtils.getSorting(sorting, criteriaBuilder, root);
				query.orderBy(orderList);
			} catch (Exception e) {
				throw new CustomException(500, e);
			}
		}
		
		Integer pageNo = page.getPageNo();
		Integer pageSize = page.getPageSize();
		if (null == pageNo) {
			errors.add(String.format(Constants.ENTITY_REQUIRED, "pageNo"));
		}
		if (null == pageSize) {
			errors.add(String.format(Constants.ENTITY_REQUIRED, "pageSize"));
		}
		
		
		if (errors.size() > 0) {
			throw new CustomException(400, errors);
		}
		try {
			
			CriteriaQuery<ReportByEvent> selectCount = query.select(root.get("id")).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			Long size = Long.valueOf(entityManager.createQuery(selectCount).getResultList().size());
			
			CriteriaQuery<ReportByEvent> select = query.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<ReportByEvent> typedQuery = entityManager.createQuery(select)
					.setFirstResult(pageNo*pageSize)
					.setMaxResults(pageSize);

			Long pages = (long) Math.ceil(size.doubleValue()/pageSize);
			return new Response<ReportByEvent>(size,typedQuery.getResultList(), pages);
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
	}
	
}
