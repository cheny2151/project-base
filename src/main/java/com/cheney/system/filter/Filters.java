package com.cheney.system.filter;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 属性过滤实体
 */
public class Filters extends ArrayList<Filter> {

    public enum Operator {

        EQ("="),

        NE("!="),

        GT(">"),

        LT("<"),

        GE(">="),

        LE("<="),

        LIKE("like"),

        NO_LIKE("not like"),

        IN("in"),

        IS_NULL("is null"),

        IS_NOT_NULL("is not null");

        private String script;

        Operator(String script) {
            this.script = script;
        }

        public String getScript() {
            return script;
        }
    }

    /**
     * 存储主表之外的字段过滤实体
     */
    private ArrayList<Filter> others;

    public Filters() {
    }

    public Filters(Filter filter) {
        super();
        add(filter);
    }

    public Filters addFilter(Filter filter) {
        this.add(filter);
        return this;
    }

    public Filters addOther(Filter filter) {
        if (this.others == null) {
            this.others = new ArrayList<>();
        }
        this.others.add(filter);
        return this;
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

    public static <T> Filter in(String property, Collection<T> value) {
        return new InFilter(property, value);
    }

    public static <T> Filter in(String property, T[] value) {
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

    public static Filters create(Filter filters) {
        return new Filters(filters);
    }

    public ArrayList<Filter> getOthers() {
        return others;
    }

    public boolean hasOthers() {
        return !CollectionUtils.isEmpty(this.others);
    }

}
