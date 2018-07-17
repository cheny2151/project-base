package com.cheney.entity.dto;

import lombok.Data;

import javax.persistence.EntityListeners;
import java.io.Serializable;
import java.util.Date;

/**
 * entity基类
 */
@EntityListeners(EntityListener.class)
@Data
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

}
