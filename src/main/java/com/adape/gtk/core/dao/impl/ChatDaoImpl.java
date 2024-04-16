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
import com.adape.gtk.core.dao.ChatDao;
import com.adape.gtk.core.dao.entity.Chat;
import com.adape.gtk.core.dao.entity.Chat.ChatId;
import com.adape.gtk.core.dao.entity.repository.ChatRepository;
import com.adape.gtk.core.utils.Constants;
import com.adape.gtk.core.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatDaoImpl implements ChatDao{
	
	@Autowired
	private ChatRepository chatRepository;
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Chat create(Chat chat) throws CustomException {
		Chat newChat = null;
		try {
			newChat = chatRepository.save(chat);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(500, e);
		}
		return newChat;
	}

	@Override
	public Chat edit(Chat chat) throws CustomException {
		Chat newChat = null;
		try {	
			newChat = chatRepository.save(chat);
		} catch (ConstraintViolationException cve) {
			throw new CustomException(400, cve.getLocalizedMessage());
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
		return newChat;
	}

	@Override
	public void delete(ChatId id) throws CustomException {
		try {	
			chatRepository.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(500, e);
		}
	}

	@Override
	public boolean existsById(ChatId id) {
		if (chatRepository.existsById(id)) {
			log.info(String.format(Constants.ENTITY_EXIST, "Chat"));
			return true;
		} else {
			log.info(String.format(Constants.ENTITY_NOT_EXIST, "Chat"));
			return false;
		}
	}
	
	@Override
	public Chat get(ChatId id) {
		Optional<Chat> optChat = chatRepository.findById(id);
		if (optChat.isEmpty()) {
			log.info(String.format(Constants.ENTITY_GET_NOT_FOUND, "Chat", "id: " + id));
			return null;
		} else {
			log.info(String.format(Constants.ENTITY_GET_SUCCESSFULLY, "Chat", "id: " + id));	
			return optChat.get();
		}
	}

	@Override
	public List<ChatId> delete(List<Chat> id) throws CustomException {
		List<ChatId> deletedIds = new ArrayList<ChatId>();
		try {
			for (Chat entity : id) {
				chatRepository.delete(entity);
				deletedIds.add(entity.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(500, e);
		}
		return deletedIds;
	}

	@Override
	public Response<Chat> get(Filter filter) throws CustomException {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Chat> query = criteriaBuilder.createQuery(Chat.class);
		Root<Chat> root = query.from(Chat.class);
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
			
			CriteriaQuery<Chat> selectCount = query.select(root.get("id")).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			Long size = Long.valueOf(entityManager.createQuery(selectCount).getResultList().size());
			
			CriteriaQuery<Chat> select = query.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<Chat> typedQuery = entityManager.createQuery(select)
					.setFirstResult(pageNo*pageSize)
					.setMaxResults(pageSize);

			Long pages = (long) Math.ceil(size.doubleValue()/pageSize);
			return new Response<Chat>(size,typedQuery.getResultList(), pages);
		} catch (Exception e) {
			throw new CustomException(500, e);
		}
	}

}
