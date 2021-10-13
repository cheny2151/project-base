package com.cheney.utils.entityCopy;

import com.cheney.entity.AuthUser;
import com.cheney.entity.TestCopyEntity;
import com.cheney.exception.EntityCopyException;
import cn.cheny.toolbox.reflect.ReflectUtils;
import com.cheney.utils.entityCopy.annotation.CopyAsClass;
import com.cheney.utils.entityCopy.annotation.CopyAsField;
import com.cheney.utils.entityCopy.meta.factory.CopyClassMetaDataFactory;
import com.cheney.utils.entityCopy.meta.factory.DefaultCopyClassMetaDataFactory;
import com.cheney.utils.meta.ClassMetaData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体属性copy工具类
 *
 * @author cheney
 * @date 2019-08-01
 */
public class EntityCopyUtils {

    private static Map<Class<?>, ClassMetaData<?>> classMetaDataMap = new ConcurrentHashMap<>();

    private static Map<Class<? extends FieldCopyAdopt>, FieldCopyAdopt> fieldCopyAdopt = new ConcurrentHashMap<>();

    private static CopyClassMetaDataFactory copyClassMetaDataFactory = new DefaultCopyClassMetaDataFactory();

    private EntityCopyUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R copyEntity(T source) {
        Class<T> sourceClass = (Class<T>) source.getClass();

        CopyAsClass copyAsClass = sourceClass.getDeclaredAnnotation(CopyAsClass.class);
        if (copyAsClass == null) {
            throw new EntityCopyException("annotation CopyAsClass required");
        }

        // 获取sourceMetaData
        ClassMetaData<T> sourceMetaData = copyClassMetaDataFactory.cacheSourceInMap(sourceClass, classMetaDataMap);

        // 获取目标类型MetaData
        Class<R> targetClass = (Class<R>) copyAsClass.as();
        ClassMetaData<R> targetMetaData = copyClassMetaDataFactory.cacheTargetInMap(targetClass, classMetaDataMap);

        // 生产target
        R target = ReflectUtils.newObject(targetClass, null, null);

        // 目标类所有字段
        Map<String, Field> targetFields = targetMetaData.getFields();

        // Field
        Map<String, Field> fields = sourceMetaData.getFields();
        for (Map.Entry<String, Field> fieldEntry : fields.entrySet()) {
            String propertyName = fieldEntry.getKey();
            Field sourceField = fieldEntry.getValue();
            Field targetField = targetFields.get(propertyName);
            if (targetField != null) {
                try {
                    CopyAsField copyAsField = sourceField.getDeclaredAnnotation(CopyAsField.class);
                    Class<? extends FieldCopyAdopt> fieldCopyAdoptClass = copyAsField.use();
                    FieldCopyAdopt fieldCopyAdopt = getFieldCopyAdopt(fieldCopyAdoptClass);
                    targetField.set(target, fieldCopyAdopt.format(sourceField.get(source)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // method
        Map<String, Method> readMethods = sourceMetaData.getReadMethods();
        for (Map.Entry<String, Method> methodEntry : readMethods.entrySet()) {
            String propertyName = methodEntry.getKey();
            Method readMethod = methodEntry.getValue();
            Field targetField = targetFields.get(propertyName);
            if (targetField != null) {
                try {
                    CopyAsField copyAsField = readMethod.getDeclaredAnnotation(CopyAsField.class);
                    Class<? extends FieldCopyAdopt> fieldCopyAdoptClass = copyAsField.use();
                    FieldCopyAdopt fieldCopyAdopt = getFieldCopyAdopt(fieldCopyAdoptClass);
                    targetField.set(target, fieldCopyAdopt.format(readMethod.invoke(source)));
                } catch (Exception e) {
                    // do nothing
                }
            }
        }

        return target;
    }

    private static FieldCopyAdopt getFieldCopyAdopt(Class<? extends FieldCopyAdopt> clazz) {
        return fieldCopyAdopt.computeIfAbsent(clazz, key -> ReflectUtils.newObject(key, null, null));
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        TestCopyEntity testCopyEntity = new TestCopyEntity();
        testCopyEntity.setV1(10L);
        testCopyEntity.setV2("username");
        AuthUser auth = copyEntity(testCopyEntity);
        System.out.println(auth);
    }


}
