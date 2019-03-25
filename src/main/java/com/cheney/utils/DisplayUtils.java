package com.cheney.utils;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * @author cheney
 */
public class DisplayUtils {

    private DisplayUtils() {
    }

    /**
     * 过滤不展示的字段，不支持级联
     *
     * @param origin        原始对象
     * @param displayFields 过滤的字段
     * @return
     */
    public static Object displayField(Object origin, String... displayFields) {
        if (origin == null) {
            return null;
        }
        if (isPrimitive(origin) || origin instanceof String) {
            return origin;
        }
        if (origin.getClass().isArray()) {
            return displayField(Arrays.asList(((Object[]) origin)), displayFields);
        } else if (origin instanceof Collection) {
            List<Object> result = new ArrayList<>();
            for (Object o : ((Collection) origin)) {
                result.add(displayField(o, displayFields));
            }
            return result;
        } else if (origin instanceof Map) {
            for (String field : displayFields) {
                ((Map) origin).remove(field);
            }
            return origin;
        } else {
            return displayField(JSON.parseObject(JSON.toJSONString(origin), Map.class), displayFields);
        }
    }

    /**
     * 判断入参是否为基础类型或者为基础类型的包装类
     * 注意:Object接收基本类型参数时，基本类型会自动装箱为基本类型的包装类型
     * 例子:int->Integer
     */
    private static boolean isPrimitive(Object object) {
        try {
            return ((Class<?>) object.getClass().getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

}
