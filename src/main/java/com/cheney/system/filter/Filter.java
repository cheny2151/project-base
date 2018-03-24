package com.cheney.system.filter;

import org.springframework.util.Assert;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Set;

/**
 * 重构:多态代替条件
 *
 * @param <T> 实体类型
 */
public abstract class Filter<T> {

    protected Class<T> javaType;

    protected Object value;

    protected String property;

    protected Boolean ignoreCase;

    private Root<T> root;

    @SuppressWarnings("unchecked")
    Filter(String property, Object value, boolean ignoreCase, Class<T> javaType) {
        this.property = property;
        this.value = value;
        this.javaType = javaType;
        this.ignoreCase = ignoreCase;
    }

    public Class<T> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<T> javaType) {
        this.javaType = javaType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public abstract void addRestrictions(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder);

    /**
     * 获取root,带缓存
     *
     * @param criteriaQuery
     * @param javaType
     * @return
     */
    @SuppressWarnings("unchecked")
    private Root<T> getRoot(CriteriaQuery<?> criteriaQuery, Class<T> javaType) {
        Assert.notNull(javaType, "Must Not Null");

        Set<Root<?>> roots = criteriaQuery.getRoots();
        for (Root<?> root : roots) {
            if (javaType.equals(root.getJavaType())) {
                Root<T> result = (Root<T>) root.as(javaType);
                //缓存
                // TODO: 2018/3/16
                return result;
            }
        }
        return null;
    }

    protected Path<?> getPath(CriteriaQuery criteriaQuery) {
        Assert.notNull(property, "Must Not Null");

        Root<T> root = this.root != null ? this.root : getRoot(criteriaQuery, javaType);
        if (root == null) {
            return null;
        }
        String[] propertySplit = property.split("\\.");
        Path<?> path = root.get(propertySplit[0]);
        for (int i = 1; i < propertySplit.length; i++) {
            path = path.get(propertySplit[i]);
        }
        return path;
    }

}
