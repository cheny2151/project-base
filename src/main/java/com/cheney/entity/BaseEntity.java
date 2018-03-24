package com.cheney.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * entity基类
 */
@MappedSuperclass
@EntityListeners(EntityListener.class)
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 9043258922225188076L;

    /**
     * id
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(updatable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
