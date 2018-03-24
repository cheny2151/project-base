package com.cheney.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色
 */
@Entity
@Table(name = "auth_role")
public class Role extends BaseEntity {

    /**
     * 用户认证枚举
     */
    public enum Authentication {

        ADMIN,

        USER,

    }

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色认证权限
     */
    private String auth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

}
