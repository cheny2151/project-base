package com.cheney.utils;

import com.cheney.exception.BeanReflectException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 反射工具类
 */
@Slf4j
public class ReflectUtils {

    private final static String GET_PRE = "get";

    private final static String SET_PRE = "set";

    private final static String IS_PRE = "is";

    private ReflectUtils() {
    }

    /**
     * 反射获取字段值
     */
    public static Object readValue(Object obj, String property) {
        try {
            return getReadMethod(obj.getClass(), property).invoke(obj);
        } catch (Exception e) {
            log.error("反射获取字段值错误", e);
            throw new BeanReflectException("反射获取字段值错误", e);
        }
    }

    /**
     * 获取read方法
     * 找不到对应方法不抛出异常，返回null
     */
    public static Method getReadMethod(Class<?> clazz, String property) {
        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException("property must not empty");
        }
        Method method;
        try {
            method = clazz.getMethod(GET_PRE + toUpperFirstLetter(property));
        } catch (NoSuchMethodException e) {
            method = null;
        }
        if (method != null) {
            return method;
        }
        try {
            method = clazz.getMethod(IS_PRE + toUpperFirstLetter(property));
        } catch (NoSuchMethodException e) {
            log.error("反射获取方法失败", e);
            throw new BeanReflectException("反射获取方法失败", e);
        }
        return method;
    }

    /**
     * 获取writer方法
     * 找不到对应方法不抛出异常，返回null
     */
    public static Method getWriterMethod(Class<?> clazz, String property, Class<?> propertyType) {
        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException("property must not empty");
        }
        try {
            return clazz.getMethod(SET_PRE + toUpperFirstLetter(property), propertyType);
        } catch (NoSuchMethodException e) {
            log.error("反射获取方法失败", e);
            throw new BeanReflectException("反射获取方法失败", e);
        }
    }

    public static List<Field> getAllFields(Class clazz, Class stop) {
        List<Field> fields = new ArrayList<>();
        for (; clazz != stop; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equalsIgnoreCase("serialVersionUID")) continue;
                fields.add(field);
            }
        }
        return fields;
    }

    public static Set<String> getAllFieldNames(Class clazz) {
        List<Field> fields = getAllFields(clazz, Object.class);
        TreeSet<String> names = new TreeSet<>();
        fields.forEach(field ->
                names.add(field.getName())
        );
        return names;
    }

    private static String toUpperFirstLetter(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

}
