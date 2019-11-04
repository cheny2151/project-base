package com.cheney.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统权限
 *
 * @author cheney
 * @date 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Auth extends BaseEntity<Integer> {

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

}
