package com.cheney.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理员
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Admin extends UserBase {

    private static final long serialVersionUID = 3043047132369017603L;

}
