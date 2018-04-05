package com.cheney.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户统一认证表
 */
@Entity
@Table(name = "auth_user", indexes = {@Index(columnList = "username")})
public class AuthUser extends BaseEntity {

    private static final long serialVersionUID = -8511714083272416828L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 角色
     */
    private Set<Role> roles = new HashSet<>();

    /**
     * 原类型id
     */
    private Long originId;

    /**
     * 最后一次修改密码的时间
     */
    private Date lastPasswordReset;

    public AuthUser() {
    }

    public AuthUser(String username, String password, boolean enabled, Set<Role> roles, Long originId, Date lastPasswordReset) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
        this.originId = originId;
        this.lastPasswordReset = lastPasswordReset;
    }

    @Column(nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = false)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "mid_user_role")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Column(nullable = false, unique = true)
    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public Date getLastPasswordReset() {
        return lastPasswordReset;
    }

    public void setLastPasswordReset(Date lastPasswordReset) {
        this.lastPasswordReset = lastPasswordReset;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", roles=" + roles +
                ", originId=" + originId +
                ", lastPasswordReset=" + lastPasswordReset +
                '}';
    }

}