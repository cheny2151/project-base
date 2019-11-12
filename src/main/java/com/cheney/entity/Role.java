package com.cheney.entity;

import com.cheney.utils.annotation.CacheKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色
 */
@Data
@CacheKey(key = "code")
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity<Long> {

    private static final long serialVersionUID = -960190065903091695L;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 拥有的权限url
     */
    private List<String> urlPatterns;

}
