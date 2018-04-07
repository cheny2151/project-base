package com.cheney.system.order;

import org.apache.commons.lang.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 * 升序
 */
public class AscOrder extends Order {

    public AscOrder(String property) {
        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException("illegal arg property");
        }
        this.property = property;
    }

    @Override
    javax.persistence.criteria.Order create(Root<?> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.asc(root.get(property));
    }

}
