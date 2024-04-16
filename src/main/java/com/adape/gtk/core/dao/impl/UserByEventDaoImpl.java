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

import com.adape.gtk.core.dao.UserByEventDao;
import com.adape.gtk.core.dao.entity.UserByEvent;
import com.adape.gtk.core.dao.entity.UserByEvent.UserByEventId;
import com.adape.gtk.core.dao.entity.repository.UserByEventRepository;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserByEventDaoImpl implements UserByEventDao{
	
	@Autowired
	private UserByEventRepository userByEventRepository;
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public UserByEvent create(UserByEvent userByEvent) throws CustomException {
		UserByEvent newUserByEvent = null;
		try {
			newUserByEvent = userByEventRepository.save(userByEvent);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(500, e);
		}
		return newUserByEvent;
	}

	@Override
	public UserByEvent edit(UserByEvent userByEvent) throws CustomException {
		UserByEvent newUserByEvent = null;
		try {	
			newUserByEvent = userByEventRepository.save(userByEvent);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newUserByEvent;
	}

	@Override
	public void delete(UserByEventId id) throws CustomException {
		try {	
			userByEventRepository.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(500, e);
		}
	}

	@Override
	public boolean existsById(UserByEventId id) {
		if (userByEventRepository.existsById(id)) {
			log.info(String.format(Constants.ENTITY_EXIST, "UserByEvent"));
			return true;
		} else {
			log.info(String.format(Constants.ENTITY_NOT_EXIST, "UserByEvent"));
			return false;
		}
	}
	
	@Override
	public UserByEvent get(UserByEventId id) {
		Optional<UserByEvent> optUserByEvent = userByEventRepository.findById(id);
		if (optUserByEvent.isEmpty()) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "UserByEvent", "id: " + id));
			return null;
		} else {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "UserByEvent", "id: " + id));	
			return optUserByEvent.get();
		}
	}

	@Override
	public List<UserByEventId> delete(List<UserByEvent> id) throws CustomException {
		List<UserByEventId> deletedIds = new ArrayList<UserByEventId>();
		try {
			for (UserByEvent entity : id) {
				userByEventRepository.delete(entity);
				deletedIds.add(entity.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(500, e);
		}
		return deletedIds;
	}

	@Override
	public Response<UserByEvent> get(Filter filter) throws CustomException {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserByEvent> query = criteriaBuilder.createQuery(UserByEvent.class);
		Root<UserByEvent> root = query.from(UserByEvent.class);
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
			
			CriteriaQuery<UserByEvent> selectCount = query.select(root.get("id")).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			Long size = Long.valueOf(entityManager.createQuery(selectCount).getResultList().size());
			
			CriteriaQuery<UserByEvent> select = query.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<UserByEvent> typedQuery = entityManager.createQuery(select)
					.setFirstResult(pageNo*pageSize)
					.setMaxResults(pageSize);

			Long pages = (long) Math.ceil(size.doubleValue()/pageSize);
			return new Response<UserByEvent>(size,typedQuery.getResultList(), pages);
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
	}

}
