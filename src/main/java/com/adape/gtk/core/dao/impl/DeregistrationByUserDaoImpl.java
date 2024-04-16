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

import com.adape.gtk.core.dao.DeregistrationByUserDao;
import com.adape.gtk.core.dao.entity.DeregistrationByUser;
import com.adape.gtk.core.dao.entity.repository.DeregistrationByUserRepository;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeregistrationByUserDaoImpl implements DeregistrationByUserDao{
	
	@Autowired
	private DeregistrationByUserRepository deregistrationByUserRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public DeregistrationByUser create(DeregistrationByUser deregistrationByUser) throws CustomException {
		DeregistrationByUser newDeregistrationByUser = null;
		try {
			newDeregistrationByUser = deregistrationByUserRepository.save(deregistrationByUser);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		}catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newDeregistrationByUser;
	}

	@Override
	public DeregistrationByUser edit(DeregistrationByUser deregistrationByUser) throws CustomException {
		DeregistrationByUser newDeregistrationByUser = null;
		try {	
			newDeregistrationByUser = deregistrationByUserRepository.save(deregistrationByUser);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newDeregistrationByUser;
	}

	@Override
	public List<Integer> delete(List<DeregistrationByUser> id) throws CustomException {
		List<Integer> deletedIds = new ArrayList<Integer>();
		try {
			for (DeregistrationByUser entity : id) {
				deregistrationByUserRepository.delete(entity);
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
		if (deregistrationByUserRepository.existsById(id)) {
			log.info(String.format(Constants.ENTITY_EXIST, "DeregistrationByUser"));
			return true;
		} else {
			log.info(String.format(Constants.ENTITY_NOT_EXIST, "DeregistrationByUser"));
			return false;
		}
	}
	
	@Override
	public DeregistrationByUser get(Integer id) {
		Optional<DeregistrationByUser> optDeregistrationByUser = deregistrationByUserRepository.findById(id);
		if (optDeregistrationByUser.isEmpty()) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "DeregistrationByUser", "id: " + id));
			return null;
		} else {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "DeregistrationByUser", "id: " + id));	
			return optDeregistrationByUser.get();
		}
	}

	@Override
	public Response<DeregistrationByUser> get(Filter filter) throws CustomException{
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeregistrationByUser> query = criteriaBuilder.createQuery(DeregistrationByUser.class);
		Root<DeregistrationByUser> root = query.from(DeregistrationByUser.class);
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
			
			CriteriaQuery<DeregistrationByUser> selectCount = query.select(root.get("id")).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			Long size = Long.valueOf(entityManager.createQuery(selectCount).getResultList().size());
			
			CriteriaQuery<DeregistrationByUser> select = query.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<DeregistrationByUser> typedQuery = entityManager.createQuery(select)
					.setFirstResult(pageNo*pageSize)
					.setMaxResults(pageSize);

			Long pages = (long) Math.ceil(size.doubleValue()/pageSize);
			return new Response<DeregistrationByUser>(size,typedQuery.getResultList(), pages);
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
	}
	
}
