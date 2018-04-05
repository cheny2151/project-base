package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NotNullFilter extends Filter {

    NotNullFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    public Predicate addRestriction(Predicate restriction, Path<?> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(restriction, criteriaBuilder.isNotNull(path));
    }
}
