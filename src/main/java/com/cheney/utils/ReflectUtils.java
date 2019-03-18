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
        String[] withToTry = {property, ""};
        if (!property.startsWith("get") && !property.startsWith("is")) {
            buildGetMethodName(withToTry, property);
        }
        for (String toTry : withToTry) {
            try {
                if ("".equals(toTry))
                    continue;
                Method method = clazz.getDeclaredMethod(toTry);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                //change to RuntimeException
            }
        }
        throw new RuntimeException("fail to reflect method:not such method");
    }

    private static void buildGetMethodName(String[] withToTry, String name) {
        name = toUpperFirstLetter(name);
        withToTry[0] = GET_PRE + name;
        withToTry[1] = IS_PRE + name;
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
            Method method = clazz.getMethod(SET_PRE + toUpperFirstLetter(property), propertyType);
            method.setAccessible(true);
            return method;
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

    /**
     * 通过反射字段获取字段值
     *
     * @param object 实例对象
     * @param name   字段名
     * @param <T>    返回类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T reflectValueByField(Object object, String name) {
        Field field = field(object.getClass(), name);
        try {
            return (T) field.get(object);
        } catch (Exception e) {
            //change to RuntimeException
            throw new RuntimeException("reflect value error", e);
        }
    }

    private static Field field(Class<?> clazz, String name) {
        try {
            if (name == null || "".equals(name)) {
                throw new IllegalArgumentException();
            }
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            //change to RuntimeException
            throw new RuntimeException("reflect field error", e);
        }
    }

    private static String toUpperFirstLetter(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

}
