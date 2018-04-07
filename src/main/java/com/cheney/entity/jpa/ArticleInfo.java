package com.cheney.entity.jpa;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 文章
 */
@Entity
@Table(name = "m_article", indexes = {@Index(columnList = "classificationId")})
public class ArticleInfo extends BaseEntity {

    private static final long serialVersionUID = 3059543850654309594L;

    /**
     * 标题
     */
    private String title;

    /**
     * 博主username
     */
    private String username;

    /**
     * 分类id
     */
    private Long classificationId;

    /**
     * mongo地址
     */
    private String mongoId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Long classificationId) {
        this.classificationId = classificationId;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }
}
