package com.cheney.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBase extends BaseEntity {

    private static final long serialVersionUID = 3896539459527873185L;

    private String username;

}
