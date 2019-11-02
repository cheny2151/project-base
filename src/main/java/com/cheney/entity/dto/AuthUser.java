package com.cheney.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户统一认证表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AuthUser extends BaseEntity<Long> {

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

}