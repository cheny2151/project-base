package com.cheney.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * entity基类
 */
@Data
public class BaseEntity<ID> implements Serializable {

    private static final long serialVersionUID = 9043258922225188076L;

    /**
     * id
     */
    private ID id;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

}
