package com.cheney.utils;

import com.cheney.exception.ReflectException;
import com.cheney.utils.tree.TreeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.reflection.ReflectionException;

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
    public static Object readValue(Object bean, String property) {
        try {
            return getReadMethod(bean.getClass(), property).invoke(bean);
        } catch (Exception e) {
            log.error("反射获取字段值错误", e);
            throw new ReflectException("反射获取字段值错误", e);
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
        String[] withToTry = {property, null};
        if (!property.startsWith("get") && !property.startsWith("is")) {
            buildGetMethodName(withToTry, property);
        }
        for (String toTry : withToTry) {
            try {
                if (toTry == null)
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
     * 反射写入字段值
     */
    public static void writeValue(Object bean, String property, Object value) {
        try {
            getWriterMethod(bean.getClass(), property, value.getClass()).invoke(bean, value);
        } catch (Exception e) {
            log.error("反射写入字段值错误", e);
            throw new ReflectException("反射写入字段值错误", e);
        }
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
            throw new ReflectException("反射获取方法失败", e);
        }
    }

    /**
     * 获取所有字段
     *
     * @param clazz 所属对象class
     * @param stop  终止递归父类
     * @return
     */
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

    /**
     * 获取所有字段名
     *
     * @param clazz 所属对象class
     * @return
     */
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

    /**
     * 获取字段
     *
     * @param clazz 所属class
     * @param name  字段名
     * @return
     */
    private static Field field(Class<?> clazz, String name) {
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException();
        }
        Class currentClass = clazz;
        // 尝试查找Field直到Object类
        while (!Object.class.equals(currentClass)) {
            try {
                Field field = currentClass.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                // try to find field on superClass
            }
            currentClass = currentClass.getSuperclass();
        }
        throw new ReflectException("can no find field '" + name + "' in " + clazz.getSimpleName());
    }

    /**
     * 反射构造对象
     *
     * @param clazz          class
     * @param parameterClass 构造函数参数class
     * @param args           构造函数参数
     * @param <T>            返回类型
     * @return
     */
    public static <T extends TreeType<T>> T newObject(Class<T> clazz, Class<?>[] parameterClass, Object[] args) {
        try {
            return parameterClass == null || parameterClass.length == 0 ?
                    clazz.getConstructor().newInstance() :
                    clazz.getConstructor(parameterClass).newInstance(args);
        } catch (Exception e) {
            throw new ReflectionException("reflect:can no new object");
        }
    }

    private static String toUpperFirstLetter(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

}
