package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class EqualFilter extends Filter {

    private Boolean ignoreCase;

    EqualFilter(String property, Object value, boolean ignoreCase) {
        super(property, value);
        this.ignoreCase = ignoreCase;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate addRestriction(Predicate restriction,Path<?> path,CriteriaBuilder criteriaBuilder) {
        if (ignoreCase && value instanceof String && String.class.isAssignableFrom(path.getJavaType())) {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(criteriaBuilder.lower((Path<String>) path), ((String) value).toLowerCase()));
        } else {
            restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(path, value));
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
