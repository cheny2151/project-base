package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class NotLikeFilter extends Filter {

    private Boolean ignoreCase;

    NotLikeFilter(String property, Object value, boolean ignoreCase) {
        super(property, value);
        this.ignoreCase = ignoreCase;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate addRestriction(Predicate restriction, Path<?> path, CriteriaBuilder criteriaBuilder) {
        if (value instanceof String && String.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.notLike((Path<String>) path, "%" + value + "%"));
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
