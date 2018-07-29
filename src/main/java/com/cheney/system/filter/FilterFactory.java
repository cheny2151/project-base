package com.cheney.system.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 过滤器工厂
 */
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

    public static Filter eq(String property, Object value) {
        return new EqualFilter(property, value);
    }

    public static Filter notEq(String property, Object value) {
        return new NotEqualFilter(property, value);
    }

    public static Filter gt(String property, Object value) {
        return new GreaterThanFilter(property, value);
    }

    public static Filter ge(String property, Object value) {
        return new GreaterThanOrEqualFilter(property, value);
    }

    public static Filter lt(String property, Object value) {
        return new LessThanFilter(property, value);
    }

    public static Filter le(String property, Object value) {
        return new LessThanOrEqualFilter(property, value);
    }

    public static Filter in(String property, Object value) {
        return new InFilter(property, value);
    }

    public static Filter like(String property, Object value) {
        return new LikeFilter(property, value);
    }

    public static Filter notLike(String property, Object value) {
        return new NotLikeFilter(property, value);
    }

    public static Filter isNull(String property) {
        return new NullFilter(property);
    }

    public static Filter isNotNull(String property) {
        return new NotNullFilter(property);
    }

    public static Filter isNotLike(String property, Object value) {
        return new NotLikeFilter(property, value);
    }

    public static List<Filter> createFilterList(Filter... filters) {
        return new ArrayList<>(Arrays.asList(filters));
    }

}
