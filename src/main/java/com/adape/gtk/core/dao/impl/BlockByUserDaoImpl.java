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

import com.adape.gtk.core.dao.BlockByUserDao;
import com.adape.gtk.core.dao.entity.BlockByUser;
import com.adape.gtk.core.dao.entity.repository.BlockByUserRepository;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BlockByUserDaoImpl implements BlockByUserDao{
	
	@Autowired
	private BlockByUserRepository blockByUserRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public BlockByUser create(BlockByUser blockByUser) throws CustomException {
		BlockByUser newBlockByUser = null;
		try {
			newBlockByUser = blockByUserRepository.save(blockByUser);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		}catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newBlockByUser;
	}

	@Override
	public BlockByUser edit(BlockByUser blockByUser) throws CustomException {
		BlockByUser newBlockByUser = null;
		try {	
			newBlockByUser = blockByUserRepository.save(blockByUser);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newBlockByUser;
	}

	@Override
	public List<Integer> delete(List<BlockByUser> id) throws CustomException {
		List<Integer> deletedIds = new ArrayList<Integer>();
		try {
			for (BlockByUser entity : id) {
				blockByUserRepository.delete(entity);
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
		if (blockByUserRepository.existsById(id)) {
			log.info(String.format(Constants.ENTITY_EXIST, "BlockByUser"));
			return true;
		} else {
			log.info(String.format(Constants.ENTITY_NOT_EXIST, "BlockByUser"));
			return false;
		}
	}
	
	@Override
	public BlockByUser get(Integer id) {
		Optional<BlockByUser> optBlockByUser = blockByUserRepository.findById(id);
		if (optBlockByUser.isEmpty()) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "BlockByUser", "id: " + id));
			return null;
		} else {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "BlockByUser", "id: " + id));	
			return optBlockByUser.get();
		}
	}

	@Override
	public Response<BlockByUser> get(Filter filter) throws CustomException{
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BlockByUser> query = criteriaBuilder.createQuery(BlockByUser.class);
		Root<BlockByUser> root = query.from(BlockByUser.class);
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
			
			CriteriaQuery<BlockByUser> selectCount = query.select(root.get("id")).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			Long size = Long.valueOf(entityManager.createQuery(selectCount).getResultList().size());
			
			CriteriaQuery<BlockByUser> select = query.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<BlockByUser> typedQuery = entityManager.createQuery(select)
					.setFirstResult(pageNo*pageSize)
					.setMaxResults(pageSize);

			Long pages = (long) Math.ceil(size.doubleValue()/pageSize);
			return new Response<BlockByUser>(size,typedQuery.getResultList(), pages);
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
	}
	
}
