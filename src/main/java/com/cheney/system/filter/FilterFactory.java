package com.cheney.system.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterFactory {

    public enum Operator {

        eq,

        ne,

        gt,

        lt,

        ge,

        le,

        like,

        noLike,

        in,

        isNull,

        isNotNull

    }

    public static <T> Filter<T> eq(String property, Object value, Class<T> javaType) {
        return new EqualFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> ne(String property, Object value, Class<T> javaType) {
        return new NotEqualFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> gt(String property, Object value, Class<T> javaType) {
        return new GreaterThanFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> ge(String property, Object value, Class<T> javaType) {
        return new GreaterThanOrEqualFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> lt(String property, Object value, Class<T> javaType) {
        return new LessThanFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> le(String property, Object value, Class<T> javaType) {
        return new LessThanOrEqualFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> in(String property, Object value, Class<T> javaType) {
        return new InFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> like(String property, Object value, Class<T> javaType) {
        return new LikeFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> isNull(String property, Object value, Class<T> javaType) {
        return new NullFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> isNotNull(String property, Object value, Class<T> javaType) {
        return new NotNullFilter<>(property, value, false, javaType);
    }

    public static <T> Filter<T> createIgnoreCase(Operator operator, String property, Object value, Class<T> javaType) {

        switch (operator) {
            case eq: {
                return new EqualFilter<>(property, value, true, javaType);
            }
            case ne: {
                return new NotEqualFilter<>(property, value, true, javaType);
            }
            case like: {
                return new LikeFilter<>(property, value, true, javaType);
            }
            case noLike: {
                return new NotLikeFilter<>(property, value, true, javaType);
            }
            default:
                throw new RuntimeException("no this operator");
        }

    }

    @SafeVarargs
    public static <T> List<Filter<T>> createFilterList(Filter<T>... filters) {
        ArrayList<Filter<T>> filterList = new ArrayList<>();
        filterList.addAll(Arrays.asList(filters));
        return filterList;
    }

}
