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

import com.adape.gtk.core.dao.CommentDao;
import com.adape.gtk.core.dao.entity.Comment;
import com.adape.gtk.core.dao.entity.repository.CommentRepository;
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
public class CommentDaoImpl implements CommentDao{
	
	@Autowired
	private CommentRepository commentRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Comment create(Comment comment) throws CustomException {
		Comment newComment = null;
		try {
			newComment = commentRepository.save(comment);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		}catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newComment;
	}

	@Override
	public Comment edit(Comment comment) throws CustomException {
		Comment newComment = null;
		try {	
			newComment = commentRepository.save(comment);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newComment;
	}

	@Override
	public List<Integer> delete(List<Comment> id) throws CustomException {
		List<Integer> deletedIds = new ArrayList<Integer>();
		try {
			for (Comment entity : id) {
				commentRepository.delete(entity);
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
		if (commentRepository.existsById(id)) {
			log.info(String.format(Constants.ENTITY_EXIST, "Comment"));
			return true;
		} else {
			log.info(String.format(Constants.ENTITY_NOT_EXIST, "Comment"));
			return false;
		}
	}
	
	@Override
	public Comment get(Integer id) {
		Optional<Comment> optComment = commentRepository.findById(id);
		if (optComment.isEmpty()) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Comment", "id: " + id));
			return null;
		} else {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Comment", "id: " + id));	
			return optComment.get();
		}
	}

	@Override
	public Response<Comment> get(Filter filter) throws CustomException{
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Comment> query = criteriaBuilder.createQuery(Comment.class);
		Root<Comment> root = query.from(Comment.class);
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
			
			CriteriaQuery<Comment> selectCount = query.select(root.get("id")).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			Long size = Long.valueOf(entityManager.createQuery(selectCount).getResultList().size());
			
			CriteriaQuery<Comment> select = query.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<Comment> typedQuery = entityManager.createQuery(select)
					.setFirstResult(pageNo*pageSize)
					.setMaxResults(pageSize);

			Long pages = (long) Math.ceil(size.doubleValue()/pageSize);
			return new Response<Comment>(size,typedQuery.getResultList(), pages);
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
	}
	
}
