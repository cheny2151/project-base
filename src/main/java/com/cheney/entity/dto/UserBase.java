package com.cheney.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBase extends BaseEntity<Long> {

    private static final long serialVersionUID = 3896539459527873185L;

    private String username;

}
