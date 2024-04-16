package com.adape.gtk.core.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.adape.gtk.core.client.beans.FilterElements;
import com.adape.gtk.core.client.beans.FilterElements.FilterType;
import com.adape.gtk.core.client.beans.FilterElements.JoinType;
import com.adape.gtk.core.client.beans.FilterElements.OperatorType;
import com.adape.gtk.core.client.beans.GroupFilter;
import com.adape.gtk.core.client.beans.GroupFilter.Operator;
import com.adape.gtk.core.client.beans.Sorting;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Slf4j
public class QueryUtils {

	static List<Class> entityTypes;
	
	static {
		entityTypes = List.of(Timestamp.class, int.class, String.class, Boolean.class, 
				BigDecimal.class, Integer.class, double.class, Double.class, boolean.class, Date.class, Year.class, Long.class, long.class);
	}
	
	public static List<Predicate> generatePredicate(GroupFilter gp, CriteriaBuilder cb, Root root,
			List<String> errors, CriteriaQuery query) {
		List<Predicate> pred = new ArrayList<Predicate>();
		Set joinsGroup = new HashSet<Join>();
		boolean useGroupRootJoin = gp.isUseGroupRootJoin();
		if (gp.getGroupFilter() != null) {
			
			
			List<Predicate> filterPredicate = new ArrayList<Predicate>();
			Predicate tmp = processGroup(gp.getGroupFilter(), cb, filterPredicate, root, errors, gp.getOperator(), query, useGroupRootJoin, joinsGroup);
			if (tmp != null)
				pred.add(tmp);
			
			if(gp.getFilterElements() != null) {
				pred.add(processFilters(gp.getFilterElements(), gp.getOperator(), root, cb, errors, query, useGroupRootJoin, joinsGroup));
			}
			
		} else if (gp.getFilterElements() != null) {
			pred.add(processFilters(gp.getFilterElements(), gp.getOperator(), root, cb, errors, query, useGroupRootJoin, joinsGroup));
		}
		return pred;

	}

	private static Predicate processGroup(List<GroupFilter> gp, CriteriaBuilder cb, List<Predicate> filterPredicate,
			Root root, List<String> errors, Operator op, CriteriaQuery query, boolean useGroupJoin, Set<Join> previousGroupJoins) {
		Predicate pred = null;
		List<Predicate> preds = new ArrayList<>();
		Set joinsGroup = new HashSet<Join>();
		if (gp.size() > 0) {
			for (GroupFilter e : gp) {
				Operator o = e.getOperator();
				List<FilterElements> filters = e.getFilterElements();
				List<GroupFilter> groups = e.getGroupFilter();
				boolean useGroupRootJoin = e.isUseGroupRootJoin();
				
				if(useGroupJoin) {
					useGroupRootJoin=true;
					joinsGroup.addAll(previousGroupJoins);
				}
				
				if (groups != null) {
					Predicate tmp = processGroup(groups, cb, filterPredicate, root, errors, o, query, useGroupRootJoin, joinsGroup);
					if (tmp != null)
						preds.add(tmp);
				} else if (filters != null) {
					preds.add(processFilters(filters, o, root, cb, errors, query, useGroupRootJoin, joinsGroup));
				}
				previousGroupJoins.addAll(joinsGroup);
			}
		}
		if (preds.size() > 0) {
			if (op.equals(Operator.OR)) {
				pred = cb.or(preds.toArray(new Predicate[preds.size()]));
			} else {
				pred = cb.and(preds.toArray(new Predicate[preds.size()]));
			}
		}

		filterPredicate = new ArrayList<>();
		return pred;

	}

	private static Predicate processFilters(List<FilterElements> filters, Operator op, Root root,
			CriteriaBuilder criteriaBuilder, List<String> errors, CriteriaQuery query, boolean useGroupRootJoin, Set<Join> previousJoinsGroup) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		Set joins = new HashSet<Join>();
		filters.forEach(f -> {
			String field = f.getKey();
			Object value = f.getValue();
			OperatorType operator = f.getOperator();
			FilterType type = f.getType();
			JoinType joinType = f.getJoinType();
			boolean useRootJoin = f.isUseRootJoin();
			
			if(useGroupRootJoin) {
				//If useGroupJoin in group -> Force root joins of filters to true
				useRootJoin=true;
				joins.addAll(previousJoinsGroup);
			}
			
			try {
				Path path = null;
				if (!type.equals(FilterType.SPECIAL)) {
					path = getPath(root, field, joinType, useRootJoin, joins);
				}
				switch (type) {
				case DOUBLE:
					switch (operator) {
					case EQUALS:
						if (value != null) {
							predicates.add(criteriaBuilder.equal(path, (Double) value));
						} else {
							predicates.add(criteriaBuilder.isNull(path));
						}
						break;
					case NOT_EQUALS:
						predicates.add(criteriaBuilder.notEqual(path, (Double) value));
						break;
					case LESS_THAN:
						predicates.add(criteriaBuilder.lessThan(path, (Double) value));
						break;
					case LESS_THAN_EQUALS:
						predicates.add(criteriaBuilder.lessThanOrEqualTo(path, (Double) value));
						break;
					case GREATER_THAN:
						predicates.add(criteriaBuilder.greaterThan(path, (Double) value));
						break;
					case GREATER_THAN_EQUALS:
						predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, (Double) value));
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case LONG:
					switch (operator) {
					case EQUALS:
						if (value != null) {
							predicates.add(criteriaBuilder.equal(path, (Long) value));
						} else {
							predicates.add(criteriaBuilder.isNull(path));
						}
						break;
					case NOT_EQUALS:
						predicates.add(criteriaBuilder.notEqual(path, (Long) value));
						break;
					case LESS_THAN:
						predicates.add(criteriaBuilder.lessThan(path, (Long) value));
						break;
					case LESS_THAN_EQUALS:
						predicates.add(criteriaBuilder.lessThanOrEqualTo(path, (Long) value));
						break;
					case GREATER_THAN:
						predicates.add(criteriaBuilder.greaterThan(path, (Long) value));
						break;
					case GREATER_THAN_EQUALS:
						predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, (Long) value));
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case INTEGER:
					switch (operator) {
					case EQUALS:
						if (value != null) {
							predicates.add(criteriaBuilder.equal(path, (Integer) value));
						} else {
							predicates.add(criteriaBuilder.isNull(path));
						}
						break;
					case NOT_EQUALS:
						predicates.add(criteriaBuilder.notEqual(path, (Integer) value));
						break;
					case CONTAINS:
						predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value + "%"));
						break;
					case ENDS_WITH:
						predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value));
						break;
					case STARTS_WITH:
						predicates.add(criteriaBuilder.like(path.as(String.class), value + "%"));
						break;
					case LESS_THAN:
						predicates.add(criteriaBuilder.lessThan(path, (Integer) value));
						break;
					case LESS_THAN_EQUALS:
						predicates.add(criteriaBuilder.lessThanOrEqualTo(path, (Integer) value));
						break;
					case GREATER_THAN:
						predicates.add(criteriaBuilder.greaterThan(path, (Integer) value));
						break;
					case GREATER_THAN_EQUALS:
						predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, (Integer) value));
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case YEAR:
					switch (operator) {
					case EQUALS:
						if (value != null) {
							predicates.add(criteriaBuilder.equal(path, Year.parse(value.toString()) ));
						} else {
							predicates.add(criteriaBuilder.isNull(path));
						}
						break;
					case NOT_EQUALS:
						predicates.add(criteriaBuilder.notEqual(path, Year.parse(value.toString())));
						break;
					case LESS_THAN:
						predicates.add(criteriaBuilder.lessThan(path,Year.parse(value.toString())));
						break;
					case LESS_THAN_EQUALS:
						predicates.add(criteriaBuilder.lessThanOrEqualTo(path, Year.parse(value.toString())));
						break;
					case GREATER_THAN:
						predicates.add(criteriaBuilder.greaterThan(path, Year.parse(value.toString())));
						break;
					case GREATER_THAN_EQUALS:
						predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, Year.parse(value.toString())));
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case BOOLEAN:
					switch (operator) {
					case EQUALS:
						predicates.add(criteriaBuilder.equal(path, (Boolean) value));
						break;
					case NOT_EQUALS:
						predicates.add(criteriaBuilder.notEqual(path, (Boolean) value));
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case STRING:
					switch (operator) {
					case EQUALS:
						predicates.add(criteriaBuilder.equal(path, value));
						break;
					case NOT_EQUALS:
						predicates.add(criteriaBuilder.notEqual(path, value));
						break;
					case CONTAINS:
						predicates.add(criteriaBuilder.like(path, "%" + value + "%"));
						break;
					case ENDS_WITH:
						predicates.add(criteriaBuilder.like(path, "%" + value));
						break;
					case STARTS_WITH:
						predicates.add(criteriaBuilder.like(path, value + "%"));
						break;
					case MATCH:
						Expression<Double> match = criteriaBuilder.function("match", Double.class, path,
								criteriaBuilder.literal(value));
						predicates.add(criteriaBuilder.greaterThan(match, 0.));
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case INTEGER_LIST:
					ArrayList<Integer> integers = (ArrayList<Integer>) value;
					switch (operator) {
					case BETWEEN:
						if (integers.size() != 2) {
							errors.add("Invalid list size. Expected 2 given " + integers.size());
						} else {
							predicates.add(criteriaBuilder.between(path, integers.get(0), integers.get(1)));
						}
						break;
					case IN:
						predicates.add(criteriaBuilder.in(path).value(integers));
						break;
					case NOT_IN:
						predicates.add(criteriaBuilder.not(criteriaBuilder.in(path).value(integers)));
						break;
					case EQUALS:
						for (Integer id : integers) {
							predicates.add(criteriaBuilder.equal(path, id));
						}
						break;
					case NOT_EQUALS:
						for (Integer id : integers) {
							predicates.add(criteriaBuilder.notEqual(path, id));
						}
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
                case LONG_LIST:
                    ArrayList<Long> longs = (ArrayList<Long>) value;
                    switch (operator) {
                    case BETWEEN:
                        if (longs.size() != 2) {
                            errors.add("Invalid list size. Expected 2 given " + longs.size());
                        } else {
                            predicates.add(criteriaBuilder.between(path, longs.get(0), longs.get(1)));
                        }
                        break;
                    case IN:
                        predicates.add(criteriaBuilder.in(path).value(longs));
                        break;
                    case NOT_IN:
                        predicates.add(criteriaBuilder.not(criteriaBuilder.in(path).value(longs)));
                        break;
                    case EQUALS:
                        for (Long id : longs) {
                            predicates.add(criteriaBuilder.equal(path, id));
                        }
                        break;
                    case NOT_EQUALS:
                        for (Long id : longs) {
                            predicates.add(criteriaBuilder.notEqual(path, id));
                        }
                        break;
                    default:
                        log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
                        errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
                        break;
                    }
                    break;
				case STRING_LIST:
					ArrayList<String> strings = (ArrayList<String>) value;
					switch (operator) {
					case BETWEEN:
						if (strings.size() != 2) {
							errors.add("Invalid list size. Expected 2 given " + strings.size());
						} else {
							predicates.add(criteriaBuilder.between(path, strings.get(0), strings.get(1)));
						}
						break;
					case IN:
						predicates.add(criteriaBuilder.in(path).value(strings));
						break;
					case NOT_IN:
						predicates.add(criteriaBuilder.not(criteriaBuilder.in(path).value(strings)));
						break;
					case EQUALS:
						for (String id : strings) {
							predicates.add(criteriaBuilder.equal(path, id));
						}
						break;
					case NOT_EQUALS:
						for (String id : strings) {
							predicates.add(criteriaBuilder.notEqual(path, id));
						}
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case DATE:
				    switch (operator) {
                    case BETWEEN:
                        ArrayList<Date> dates = (ArrayList<Date>) value;
                        if (dates.size() != 2) {
                            errors.add("Invalid list size. Expected 2 given " + dates.size());
                        } else {
                        	if(!dates.get(0).equals(dates.get(1))) {
                        		 predicates.add(criteriaBuilder.between(path, dates.get(0), dates.get(1)));
                        	} else {
                        		 predicates.add(criteriaBuilder.equal(path, dates.get(0)));
                        	}
                           
                        }
                        break;
                    case GREATER_THAN:
                        predicates.add(criteriaBuilder.greaterThan(path, (Date) value));
                        break;
                    case GREATER_THAN_EQUALS:
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, (Date) value));
                        break;
                    case LESS_THAN:
                        predicates.add(criteriaBuilder.lessThan(path, (Date) value));
                        break;
                    case LESS_THAN_EQUALS:
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(path, (Date)value));
                        break;
                    case EQUALS:
                        predicates.add(criteriaBuilder.equal(path, (Date) value));
                        break;
                    default:
                        log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
                        errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
                        break;
                    }
                    break;
                case DATE_STRING:
					switch (operator) {
					case BETWEEN:
						ArrayList<String> dates = (ArrayList<String>) value;
						if (dates.size() != 2) {
							errors.add("Invalid list size. Expected 2 given " + dates.size());
						} else {
							if (!dates.get(0).equals(dates.get(1))) {
								predicates.add(
										criteriaBuilder.between(path, parseDate(dates.get(0)), parseDate(dates.get(1))));
                       	} else {
                       		predicates.add(
									criteriaBuilder.equal(path, parseDate(dates.get(0))));
                       	}							
						}
						break;
					case GREATER_THAN:
						predicates.add(criteriaBuilder.greaterThan(path, parseDate(value)));
						break;
					case GREATER_THAN_EQUALS:
						predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, parseDate(value)));
						break;
					case LESS_THAN:
						predicates.add(criteriaBuilder.lessThan(path, parseDate(value)));
						break;
					case LESS_THAN_EQUALS:
						predicates.add(criteriaBuilder.lessThanOrEqualTo(path, parseDate(value)));
						break;
					case EQUALS:
						predicates.add(criteriaBuilder.equal(path, parseDate(value)));
						break;
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				case SPECIAL:
					switch (operator) {
					case IS_NULL:
						path = getPath(root, field, joinType, useRootJoin, joins);
						predicates.add(criteriaBuilder.isNull(path));
						break;
					case IS_NOT_NULL:
						path = getPath(root, field, joinType, useRootJoin, joins);
						predicates.add(criteriaBuilder.isNotNull(path));
						break;
					case EXISTS:
					case NOT_EXISTS:
					default:
						log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
						errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
						break;
					}
					break;
				default:
					log.warn(String.format(Constants.INVALID_OPERATOR, operator, type));
					errors.add(String.format(Constants.INVALID_OPERATOR, operator, type));
					break;
				}
			} catch (Exception e) {
				errors.add(e.toString());
			}
			
			previousJoinsGroup.addAll(joins);
		});
		if (op.equals(Operator.OR))
			return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
		return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
	}


	private static Date parseDate(Object date) throws ParseException {
		SimpleDateFormat[] formats = new SimpleDateFormat[] { new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"),
				new SimpleDateFormat("dd-MM-yyyy"),
				new SimpleDateFormat("dd/MM/yyyy")};
		Date parsedDate = null;
		for (int i = 0; i < formats.length; i++) {
			try {
				parsedDate = formats[i].parse((String) date);
				return parsedDate;
			} catch (ParseException e) {
				continue;
			}
		}
		throw new ParseException("Unknown date format: '" + date + "'", 0);
	}

	public static Path getPath(Root<?> root, String attributeName, JoinType joinType, boolean useRootJoin, Set<Join> prevJoins) {
		Path path = root;
		Join join = null;
		Set joins = new HashSet<Join>();
		if (useRootJoin)
			joins.addAll(prevJoins);
		
		for (String part : attributeName.split("\\.")) {
			if (path.get(part).getJavaType().isInterface() || !entityTypes.contains(path.get(part).getJavaType())) {
				boolean skip = false;
				for (Iterator iterator = joins.iterator(); iterator.hasNext();) {
					Join type = (Join) iterator.next();
					if (type.getAttribute().getJavaType().equals(path.get(part).getJavaType()) 
							&& type.getAttribute().getName().equals(part)) {
						join = type;
						skip = true;
					}
				}
				if (path instanceof Join && !skip) {
					if (joinType != null) {
						join = ((Join) path).join(part,jakarta.persistence.criteria.JoinType.valueOf(joinType.name()));
					} else {
						join = ((Join) path).join(part);
					}
					joins.add(join);
					skip = true;
				}
				if (!skip) {
					if (joinType != null) {
						path = root.join(part,jakarta.persistence.criteria.JoinType.valueOf(joinType.name()));
					}else {
						path = root.join(part);
					}
					
					joins.addAll(root.getJoins());
				}
				else {
					path = join;
				}
				join = null;
			}
			else {
				if (join != null)
					path = join;
				path = path.get(part);
			}
		}
		prevJoins.addAll(joins);
		path.alias(attributeName);
		return path;
	}

	public static List<Order> getSorting(List<Sorting> sorting, CriteriaBuilder criteriaBuilder, Root<?> root) {
		List<Order> orderList = new ArrayList<Order>();
		for (Sorting sort : sorting) {
			Path path = getPath(root, sort.getField(), null, true, new HashSet<>());
			if (sort.getOrder().equals(Sorting.Order.ASC)) {
				orderList.add(criteriaBuilder.asc(path));
			} else {
				orderList.add(criteriaBuilder.desc(path));
			}
		}
		return orderList;
	}
}
