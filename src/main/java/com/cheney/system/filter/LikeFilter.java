package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class LikeFilter<T> extends Filter<T> {

    LikeFilter(String property, Object value, boolean ignoreCase, Class<T> javaType) {
        super(property, value, ignoreCase, javaType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addRestrictions(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Path<?> path = getPath(criteriaQuery);
        if (value instanceof String && String.class.isAssignableFrom(path.getJavaType())) {
            Predicate restriction = criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder.conjunction();
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.like((Path<String>) path, "%" + value + "%"));
            criteriaQuery.where(restriction);
        }
    }
}
