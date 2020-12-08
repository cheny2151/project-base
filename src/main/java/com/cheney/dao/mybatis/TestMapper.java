package com.cheney.dao.mybatis;

import com.cheney.utils.mybatis.plugin.routing.Routing;
import com.cheney.utils.mybatis.plugin.routing.RoutingParam;

import java.util.List;
import java.util.Map;

@Routing(table = "test", dependColumn = "code")
public interface TestMapper {

    @RoutingParam(paramIndex = 1, paramType = RoutingParam.ParamType.MAP, field = "date")
    List<Map<String, Object>> test(String test2, Map<String, Object> map, String test);
}
