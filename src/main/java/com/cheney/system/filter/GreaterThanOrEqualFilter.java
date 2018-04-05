package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Date;

public class GreaterThanOrEqualFilter extends Filter {

    GreaterThanOrEqualFilter(String property, Object value) {
        super(property, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate addRestriction(Predicate restriction, Path<?> path, CriteriaBuilder criteriaBuilder) {
        if (value instanceof Number && Number.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.ge((Path<Number>) path, (Number) value));
        } else if (value instanceof Date && Date.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.greaterThanOrEqualTo((Path<Date>) path, (Date) value));
        }
        return restriction;
    }
}
