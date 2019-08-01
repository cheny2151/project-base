package com.cheney.entity.dto;

import com.cheney.utils.entityCopy.annotation.CopyAsClass;
import com.cheney.utils.entityCopy.annotation.CopyAsField;
import lombok.Data;

/**
 * @author cheney
 * @date 2019-08-01
 */
@Data
@CopyAsClass(as = AuthUser.class)
public class TestCopyEntity {

    @CopyAsField(as = "id")
    private Long v1;

    @CopyAsField(as = "username")
    private String v2;

}
