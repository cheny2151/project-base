package com.cheney.system.order;

import org.apache.commons.lang.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 * 降序
 */
public class DescOrder extends Order {

    public DescOrder(String property) {
        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException("illegal arg property");
        }
        this.property = property;
    }

    @Override
    javax.persistence.criteria.Order create(Root<?> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.desc(root.get(property));
    }

}
