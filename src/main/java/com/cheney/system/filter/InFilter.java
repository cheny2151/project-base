package com.cheney.system.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Collection;

public class InFilter extends Filter {

    InFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    public Predicate addRestriction(Predicate restriction, Path<?> path, CriteriaBuilder criteriaBuilder) {
        //只接收数组或者集合
        Collection values;
        if (value instanceof Collection) {
            values = (Collection) value;
        } else if (value instanceof Object[]) {
            values = Arrays.asList((Object[]) value);
        } else {
            throw new IllegalArgumentException("illegal filter value,must collection or array");
        }
        return criteriaBuilder.and(restriction, path.in(values));
    }
}
