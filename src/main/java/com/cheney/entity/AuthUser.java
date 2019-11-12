package com.cheney.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
     * 源用户id
     */
    private Long originId;

    /**
     * 角色
     */
    private Set<String> roles;

}