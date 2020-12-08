package com.cheney.utils.mybatis.plugin.routing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author cheney
 * @date 2020-11-29
 */
@Slf4j
public class BandingRoutingInterceptor implements Interceptor {

    private static final Class<Executor> TARGET_INTERFACE = Executor.class;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object plugin(Object target) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length == 0 || !ArrayUtils.contains(interfaces, TARGET_INTERFACE)) {
            return target;
        }
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{TARGET_INTERFACE}, new BandingRoutingPlugin(target));
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
