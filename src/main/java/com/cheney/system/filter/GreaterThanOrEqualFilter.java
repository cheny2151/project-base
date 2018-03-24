package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Date;

public class GreaterThanOrEqualFilter<T> extends Filter<T> {

    GreaterThanOrEqualFilter(String property, Object value, boolean ignoreCase, Class<T> javaType) {
        super(property, value, ignoreCase, javaType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addRestrictions(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate restriction = criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder.conjunction();
        Path<?> path = getPath(criteriaQuery);
        if (value instanceof Number && Number.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.ge((Path<Number>) path, (Number) value));
        } else if (value instanceof Date && Date.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.greaterThanOrEqualTo((Path<Date>) path, (Date) value));
        }
        criteriaQuery.where(restriction);
    }
}
