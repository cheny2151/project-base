package com.cheney.entity.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 文章分类
 */
@Entity
@Table(name = "m_classification")
public class Classification extends BaseEntity {

    private static final long serialVersionUID = 6072249757608563262L;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 博主id
     */
    private String bloggerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloggerId() {
        return bloggerId;
    }

    public void setBloggerId(String bloggerId) {
        this.bloggerId = bloggerId;
    }

}
