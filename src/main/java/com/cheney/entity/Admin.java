package com.cheney.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 管理员
 */
@Entity
@Table(name = "m_admin")
public class Admin extends UserBase {

    private static final long serialVersionUID = 3043047132369017603L;

}
