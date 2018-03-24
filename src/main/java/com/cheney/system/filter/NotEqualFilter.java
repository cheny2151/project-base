package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NotEqualFilter<T> extends Filter<T> {

    NotEqualFilter(String property, Object value, boolean ignoreCase, Class<T> javaType) {
        super(property, value, ignoreCase, javaType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addRestrictions(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate restriction = criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder.conjunction();
        Path<?> path = getPath(criteriaQuery);
        if (ignoreCase && value instanceof String && String.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.notEqual(criteriaBuilder.lower((Path<String>) path), ((String) value).toLowerCase()));
        } else {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.notEqual(path, value));
        }
        criteriaQuery.where(restriction);
    }


}
