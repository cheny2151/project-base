package com.cheney.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity<Long> {

    private static final long serialVersionUID = -960190065903091695L;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色认证权限
     */
    private List<String> authCode;

}
