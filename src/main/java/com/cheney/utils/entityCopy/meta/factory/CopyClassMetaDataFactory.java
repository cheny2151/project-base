package com.cheney.utils.entityCopy.meta.factory;

import com.cheney.utils.meta.ClassMetaData;

import java.util.Map;

/**
 * @author cheney
 * @date 2019-08-01
 */
public interface CopyClassMetaDataFactory {

    <T> ClassMetaData<T> createSourceMetaData(Class<T> clazz);

    <T> ClassMetaData<T> createTargetMetaData(Class<T> clazz);

    <T> ClassMetaData<T> cacheSourceInMap(Class<T> clazz, Map<Class<?>, ClassMetaData<?>> map);

    <T> ClassMetaData<T> cacheTargetInMap(Class<T> clazz, Map<Class<?>, ClassMetaData<?>> map);
}
