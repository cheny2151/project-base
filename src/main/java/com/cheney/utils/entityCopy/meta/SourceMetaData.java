package com.cheney.utils.entityCopy.meta;

import com.cheney.utils.ReflectUtils;
import com.cheney.utils.entityCopy.annotation.CopyAsField;
import com.cheney.utils.meta.ClassMetaData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实体复制源数据元
 *
 * @author cheney
 * @date 2019-08-01
 */
public class SourceMetaData<T> extends ClassMetaData<T> {

    public SourceMetaData(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void load() {
        Map<String, Method> hasCopayAsFieldMethod = ReflectUtils.getAllReadMethodHasAnnotation(super.getClazz(), CopyAsField.class);
        hasCopayAsFieldMethod = hasCopayAsFieldMethod.values().stream()
                .collect(Collectors.toMap(method -> method.getDeclaredAnnotation(CopyAsField.class).as(), method -> method));
        this.addReadMethod(hasCopayAsFieldMethod);
        Map<String, Field> hasCopayAsFieldField = ReflectUtils.getAllFieldHasAnnotation(super.getClazz(), CopyAsField.class);
        hasCopayAsFieldField = hasCopayAsFieldField.values().stream()
                .collect(Collectors.toMap(field -> field.getDeclaredAnnotation(CopyAsField.class).as(), field -> field));
        this.addFields(hasCopayAsFieldField);
    }

}
