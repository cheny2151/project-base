package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

public class NullFilter<T> extends Filter<T> {

    NullFilter(String property, Object value, boolean ignoreCase, Class<T> javaType) {
        super(property, value, ignoreCase, javaType);
    }

    @Override
    public void addRestrictions(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate restriction = criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder.conjunction();
        restriction = criteriaBuilder.and(restriction, criteriaBuilder.isNull(getPath(criteriaQuery)));
        criteriaQuery.where(restriction);
    }
}
