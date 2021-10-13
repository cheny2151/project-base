package com.cheney.utils.entityCopy.meta;

import cn.cheny.toolbox.reflect.ReflectUtils;
import com.cheney.utils.meta.ClassMetaData;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实体复制目标数据元
 *
 * @author cheney
 * @date 2019-08-01
 */
public class TargetMetaData<T> extends ClassMetaData<T> {

    public TargetMetaData(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void load() {
        List<Field> allFields = ReflectUtils.getAllFields(super.getClazz(), Object.class);
        Map<String, Field> fieldMap = allFields.stream().collect(Collectors.toMap(Field::getName, field -> field));
        this.addFields(fieldMap);
    }
}
