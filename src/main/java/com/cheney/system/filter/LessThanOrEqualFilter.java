package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Date;

public class LessThanOrEqualFilter extends Filter {

    LessThanOrEqualFilter(String property, Object value) {
        super(property, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate addRestriction(Predicate restriction, Path<?> path, CriteriaBuilder criteriaBuilder) {
        if (value instanceof Number && Number.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.le((Path<Number>) path, (Number) value));
        } else if (value instanceof Date && Date.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.lessThanOrEqualTo((Path<Date>) path, (Date) value));
        }
        return restriction;
    }
}
