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
        hasCopayAsFieldMethod = hasCopayAsFieldMethod.entrySet().stream()
                .collect(Collectors.toMap(entity -> {
                    String propertyName = entity.getKey();
                    Method method = entity.getValue();
                    String as = method.getDeclaredAnnotation(CopyAsField.class).as();
                    // as为空则默认使用属性名
                    return "".equals(as) ? propertyName : as;
                }, Map.Entry::getValue));
        this.addReadMethod(hasCopayAsFieldMethod);

        Map<String, Field> hasCopayAsFieldField = ReflectUtils.getAllFieldHasAnnotation(super.getClazz(), CopyAsField.class);
        hasCopayAsFieldField = hasCopayAsFieldField.entrySet().stream()
                .collect(Collectors.toMap(entity -> {
                    String propertyName = entity.getKey();
                    Field field = entity.getValue();
                    String as = field.getDeclaredAnnotation(CopyAsField.class).as();
                    // as为空则默认使用属性名
                    return "".equals(as) ? propertyName : as;
                }, Map.Entry::getValue));
        this.addFields(hasCopayAsFieldField);
    }

}
