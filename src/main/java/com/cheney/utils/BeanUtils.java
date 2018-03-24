package com.cheney.utils;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class BeanUtils {

    private BeanUtils() {
    }

    /**
     * 反射获取字段值
     */
    public static Object readValue(Object obj, String fieldName) {
        try {
            Method method = obj.getClass().getMethod("get" + toUpperFirstLetter(fieldName));
            obj = method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Method getReadMethod(Class<?> clazz, String property) {
        try {
            return StringUtils.isNotEmpty(property) ? clazz.getMethod(property) : null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getWriterMethod(Class<?> clazz, String property, Class<?> propertyType) {
        try {
            return StringUtils.isNotEmpty(property) ? clazz.getMethod("set" + toUpperFirstLetter(property), propertyType) : null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String toUpperFirstLetter(String fieldName) {

        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

    }

}
