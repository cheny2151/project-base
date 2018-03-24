package com.cheney.system.page;

/**
 * 分页筛选类
 * Created by cheny on 2017/9/24.
 */
public class Filter {

    public enum Operator {

        equal,

        notEqual,

        gt,

        ge,

        lt,

        le,

        in,

        like,

        notLike,

        between,

        isNull,

        notNull

    }

    /**
     * 运算符
     */
    private Operator operator;

    /**
     * 属性
     */
    private String property;

    /**
     * 值
     */
    private Object value;

    /**
     * 是否忽略大小写，默认false
     */
    private boolean isIgnoreCase = false;

    public Filter(Operator operator, String property, Object value, boolean isIgnoreCase) {
        this.operator = operator;
        this.property = property;
        this.value = value;
        this.isIgnoreCase = isIgnoreCase;
    }

    public Filter(Operator operator, String property, Object value) {
        this.operator = operator;
        this.property = property;
        this.value = value;
    }

    public static Filter eq(String property, Object value) {
        return new Filter(Operator.equal, property, value);
    }

    public static Filter noteq(String property, Object value) {
        return new Filter(Operator.notEqual, property, value);
    }

    public static Filter gt(String property, Object value) {
        return new Filter(Operator.gt, property, value);
    }

    public static Filter ge(String property, Object value) {
        return new Filter(Operator.ge, property, value);
    }

    public static Filter lt(String property, Object value) {
        return new Filter(Operator.lt, property, value);
    }

    public static Filter le(String property, Object value) {
        return new Filter(Operator.le, property, value);
    }

    public static Filter in(String property, Object value) {
        return new Filter(Operator.in, property, value);
    }

    public static Filter like(String property, Object value) {
        return new Filter(Operator.like, property, value);
    }

    public static Filter notLike(String property, Object value) {
        return new Filter(Operator.notLike, property, value);
    }

    public static Filter isNull(String property) {
        return new Filter(Operator.isNull, property, null);
    }

    public static Filter notNull(String property) {
        return new Filter(Operator.notNull, property, null);
    }

    public static Filter between(String property, Object value) {
        return new Filter(Operator.between, property, value);
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public boolean getIsIgnoreCase() {
        return isIgnoreCase;
    }

    public void setIsIgnoreCase(boolean ignoreCase) {
        isIgnoreCase = ignoreCase;
    }

}
