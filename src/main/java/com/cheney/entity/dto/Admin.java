package com.cheney.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 管理员
 */
@Table(name = "m_admin")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Admin extends UserBase {

    private static final long serialVersionUID = 3043047132369017603L;

}
