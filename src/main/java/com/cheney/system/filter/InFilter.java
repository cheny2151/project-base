package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Collection;

public class InFilter<T> extends Filter<T> {

    InFilter(String property, Object value, boolean ignoreCase, Class<T> javaType) {
        super(property, value, ignoreCase, javaType);
    }

    @Override
    public void addRestrictions(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate restriction = criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder.conjunction();
        if (value == null) {
            return;
        }
        Collection value;
        //只接收数组或者集合
        try {
            value = (Collection) this.value;
        } catch (ClassCastException e) {
            value = Arrays.asList(this.value);
        }
        restriction = criteriaBuilder.and(restriction, getPath(criteriaQuery).in(value));
        criteriaQuery.where(restriction);
    }
}
