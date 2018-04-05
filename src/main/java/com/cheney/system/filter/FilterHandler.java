package com.cheney.system.filter;

import com.cheney.utils.SpringUtils;

import javax.persistence.criteria.*;
import java.util.Collection;

/**
 * Filter处理工具类
 */
public class FilterHandler {

    private final static CriteriaBuilder criteriaBuilder;

    static {
        criteriaBuilder = (CriteriaBuilder) SpringUtils.getBean("criteriaBuilder");
    }

    public static void filterQuery(CriteriaQuery<?> criteriaQuery, Root<?> root, Collection<Filter> filters) {
        if (filters == null) throw new NullPointerException();
        Predicate restriction = criteriaQuery.getRestriction();
        if (restriction == null) {
            restriction = criteriaBuilder.conjunction();
        }
        for (Filter filter : filters) {
            restriction = filter.addRestriction(restriction, getPath(root, filter.getProperty()), criteriaBuilder);
        }
        criteriaQuery.where(restriction);
    }

    public static void filterQuery(CriteriaQuery<?> criteriaQuery, Root<?> root, Filter filter) {
        if (filter == null) throw new NullPointerException();
        Predicate restriction = criteriaQuery.getRestriction();
        if (restriction == null) {
            restriction = criteriaBuilder.conjunction();
        }
        criteriaQuery.where(filter.addRestriction(restriction, getPath(root, filter.getProperty()), criteriaBuilder));
    }

    private static Path<?> getPath(Root<?> root, String property) {
        String[] properties = property.split("\\.");
        Path<?> path = root.get(properties[0]);
        for (int i = 1; i < properties.length; i++) {
            path = path.get(properties[i]);
        }
        return path;
    }

}
