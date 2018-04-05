package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NotEqualFilter extends Filter {

    private Boolean ignoreCase;

    NotEqualFilter(String property, Object value, boolean ignoreCase) {
        super(property, value);
        this.ignoreCase = ignoreCase;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate addRestriction(Predicate restriction, Path<?> path, CriteriaBuilder criteriaBuilder) {
        if (ignoreCase && value instanceof String && String.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.notEqual(criteriaBuilder.lower((Path<String>) path), ((String) value).toLowerCase()));
        } else {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.notEqual(path, value));
        }
        return restriction;
    }

    public Boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
}
