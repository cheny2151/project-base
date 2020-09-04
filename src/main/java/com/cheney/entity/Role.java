package com.cheney.entity;

import cn.cheny.toolbox.entityCache.annotation.CacheEntity;
import cn.cheny.toolbox.entityCache.annotation.CacheField;
import cn.cheny.toolbox.entityCache.annotation.CacheFilter;
import cn.cheny.toolbox.entityCache.annotation.CacheId;
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
@CacheEntity(tableName = "sys_role", sqlFilter = {@CacheFilter("code != null"), @CacheFilter("code != ''")})
public class Role extends BaseEntity<Long> {

    private static final long serialVersionUID = -960190065903091695L;

    /**
     * 角色名
     */
    @CacheField
    private String name;

    /**
     * 角色编码
     */
    @CacheId
    private String code;

    /**
     * 拥有的权限url
     */
    @CacheField
    private List<String> urlPatterns;

}
