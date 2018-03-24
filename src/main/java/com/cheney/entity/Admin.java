package com.cheney.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 管理员
 */
@Entity
@Table(name = "m_admin")
public class Admin extends BaseEntity {

    private static final long serialVersionUID = 3043047132369017603L;

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
