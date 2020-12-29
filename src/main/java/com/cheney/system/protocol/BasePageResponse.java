package com.cheney.system.protocol;

import cn.cheny.toolbox.other.page.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BasePageResponse<T> extends BaseResponse<T> {
    private Page<T> page;

}
