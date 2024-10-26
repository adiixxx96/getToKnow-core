package com.adape.gtk.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.adape.gtk.core.dao.EventDao;
import com.adape.gtk.core.dao.entity.Event;
import com.adape.gtk.core.dao.entity.User;
import com.adape.gtk.core.dao.entity.repository.EventRepository;
import com.adape.gtk.core.client.beans.CustomException;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.GroupFilter;
import com.adape.gtk.core.client.beans.Page;
import com.adape.gtk.core.client.beans.Response;
import com.adape.gtk.core.client.beans.Sorting;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventDaoImpl implements EventDao{
	
	@Autowired
	private EventRepository eventRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Event create(Event event) throws CustomException {
		Event newEvent = null;
		try {
			newEvent = eventRepository.save(event);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		}catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newEvent;
	}

	@Override
	public Event edit(Event event) throws CustomException {
		Event newEvent = null;
		try {	
			newEvent = eventRepository.save(event);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newEvent;
	}

	@Override
	public List<Integer> delete(List<Event> id) throws CustomException {
		List<Integer> deletedIds = new ArrayList<Integer>();
		try {
			for (Event entity : id) {
				eventRepository.delete(entity);
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
		if (eventRepository.existsById(id)) {
			log.info(String.format(Constants.ENTITY_EXIST, "Event"));
			return true;
		} else {
			log.info(String.format(Constants.ENTITY_NOT_EXIST, "Event"));
			return false;
		}
	}
	
	@Override
	public Event get(Integer id) {
		Optional<Event> optEvent = eventRepository.findById(id);
		if (optEvent.isEmpty()) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Event", "id: " + id));
			return null;
		} else {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Event", "id: " + id));	
			return optEvent.get();
		}
	}

	@Override
	public Response<Event> get(Filter filter) throws CustomException{
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);
		CriteriaQuery<Integer> cq = criteriaBuilder.createQuery(Integer.class);
		Root<Event> root = query.from(Event.class);
		Root<Event> rootCount = cq.from(Event.class);
		List<Predicate> predicates = new ArrayList<>();
		List<Predicate> predicatesCount = new ArrayList<>();
		GroupFilter filters = filter.getGroupFilter();
		Page page = filter.getPage();
		List<Sorting> sorting = filter.getSorting();
		List<String> errors = new ArrayList<String>();
		
		predicates = QueryUtils.generatePredicate(filters, criteriaBuilder, root, errors, query);
		predicatesCount = QueryUtils.generatePredicate(filters, criteriaBuilder, rootCount, errors, cq);
		
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
			
			CriteriaQuery<Integer> selectCount = cq.select(rootCount.get("id")).distinct(true).where(predicatesCount.toArray(new Predicate[predicatesCount.size()]));
			Long size = Long.valueOf(entityManager.createQuery(selectCount).getResultList().size());
			
			CriteriaQuery<Event> select = query.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<Event> typedQuery = entityManager.createQuery(select)
					.setFirstResult(pageNo*pageSize)
					.setMaxResults(pageSize);

			Long pages = (long) Math.ceil(size.doubleValue()/pageSize);
			return new Response<Event>(size,typedQuery.getResultList(), pages);
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
	}
	
	@Override
	public List<Integer> getEventIdsByBody(List<String> words) {
	    String sql = "SELECT id FROM event ";
	    List<String> conditions = new ArrayList<>();
	        
	    for (String word : words) {
	     conditions.add("(title LIKE '%"+word+"%' OR description LIKE '%"+word+"%')");
	    }       
	    if (!conditions.isEmpty()) {
	        sql += "WHERE " + String.join(" AND ", conditions);
	    }
	    
	    Query query = entityManager.createNativeQuery(sql);
	  
	    List<Object> resultList = query.getResultList();
	    List<Integer> resultIds = new ArrayList<Integer>();
	    resultIds = resultList.stream().map(o -> (Integer) o).collect(Collectors.toList());

		return resultIds;
	}
	
	@Override
	public List<Integer> getEventIdsByParticipantsNumber(int min, int max) {
	    String sql = "SELECT event_id FROM user_by_event WHERE participant = 1 AND deregistration_date IS NULL ";	    
	    if (min != 0 && max != 100) {
	    	sql += "GROUP BY event_id HAVING COUNT(*) >= "+min+" AND COUNT(*) <= "+max;
	    } else if (min != 0) {
	    	sql += "GROUP BY event_id HAVING COUNT(*) >= "+min;
	    } else {
	    	sql += "GROUP BY event_id HAVING COUNT(*) <= "+max;
	    }
	    
	    Query query = entityManager.createNativeQuery(sql);
	  
	    List<Object> resultList = query.getResultList();
	    List<Integer> resultIds = new ArrayList<Integer>();
	    resultIds = resultList.stream().map(o -> (Integer) o).collect(Collectors.toList());

		return resultIds;
	}
	
	
}
