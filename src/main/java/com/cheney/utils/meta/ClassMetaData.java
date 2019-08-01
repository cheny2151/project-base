package com.cheney.utils.meta;

import com.cheney.utils.ReflectUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类元数据
 *
 * @author cheney
 * @date 2019-08-01
 */
@Data
public abstract class ClassMetaData<T> {

    /**
     * 元数据的类
     */
    private Class<T> clazz;

    /**
     * 读方法缓存
     */
    private Map<String, Method> readMethods = new ConcurrentHashMap<>();

    /**
     * 字段缓存
     */
    private Map<String, Field> fields = new ConcurrentHashMap<>();

    public ClassMetaData(Class<T> clazz) {
        this.clazz = clazz;
        this.load();
    }

    public abstract void load();

    public Field getField(String name) {
        return fields.computeIfAbsent(name, (key) -> ReflectUtils.field(clazz, name));
    }

    public Method gerReadMethod(String name) {
        return readMethods.computeIfAbsent(name, (key) -> ReflectUtils.getReadMethod(clazz, name));
    }

    public void addReadMethod(Map<String, Method> readMethods) {
        this.readMethods.putAll(readMethods);
    }

    public void addFields(Map<String, Field> fieldMap) {
        this.fields.putAll(fieldMap);
    }

}
