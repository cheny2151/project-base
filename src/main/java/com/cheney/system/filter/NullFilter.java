package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NullFilter extends Filter {

    NullFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    public Predicate addRestriction(Predicate restriction, Path<?> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(restriction, criteriaBuilder.isNull(path));
    }
}
