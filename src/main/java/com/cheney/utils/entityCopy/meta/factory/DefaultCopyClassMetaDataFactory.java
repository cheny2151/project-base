package com.cheney.utils.entityCopy.meta.factory;

import com.cheney.utils.entityCopy.meta.SourceMetaData;
import com.cheney.utils.entityCopy.meta.TargetMetaData;
import com.cheney.utils.meta.ClassMetaData;

import java.util.Map;

/**
 * @author cheney
 * @date 2019-08-01
 */
public class DefaultCopyClassMetaDataFactory implements CopyClassMetaDataFactory {

    @Override
    public <T> ClassMetaData<T> createSourceMetaData(Class<T> clazz) {
        return new SourceMetaData<>(clazz);
    }

    @Override
    public <T> ClassMetaData<T> createTargetMetaData(Class<T> clazz) {
        return new TargetMetaData<>(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ClassMetaData<T> cacheSourceInMap(Class<T> clazz, Map<Class<?>, ClassMetaData<?>> map) {
        return (ClassMetaData<T>) map.computeIfAbsent(clazz, this::createSourceMetaData);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ClassMetaData<T> cacheTargetInMap(Class<T> clazz, Map<Class<?>, ClassMetaData<?>> map) {
        return (ClassMetaData<T>) map.computeIfAbsent(clazz, this::createTargetMetaData);
    }
}
