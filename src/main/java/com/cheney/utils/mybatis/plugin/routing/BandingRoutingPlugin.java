package com.cheney.utils.mybatis.plugin.routing;

import cn.cheny.toolbox.reflect.ReflectUtils;
import com.cheney.utils.mybatis.plugin.routing.format.RoutingFormatter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author cheney
 * @date 2020-11-29
 */
@Slf4j
public class BandingRoutingPlugin implements InvocationHandler {

    private static final String MYBATIS_GENERIC_NAME_PREFIX = "param";

    private static final ConcurrentHashMap<Class<?>, RoutingInfo> ROUTING_INFOS = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<? extends RoutingFormatter>, RoutingFormatter> FORMATTERS = new ConcurrentHashMap<>();

    private Object target;

    public BandingRoutingPlugin(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("query") || methodName.equals("update")) {
            MappedStatement mappedStatement = (MappedStatement) args[0];
            // 从mapperStatement中提取对应的Mapper class
            Class<?> mapperClass = extractClass(mappedStatement);
            if (mapperClass != null && mapperClass.isAnnotationPresent(Routing.class)) {
                // 存在Routing注解才执行逻辑
                RoutingInfo routingInfo = ROUTING_INFOS.computeIfAbsent(mapperClass, c -> {
                    Routing routing = mapperClass.getDeclaredAnnotation(Routing.class);
                    RoutingFormatter routingFormatter = FORMATTERS.computeIfAbsent(routing.formatter(),
                            k -> ReflectUtils.newObject(k, null, null));
                    return new RoutingInfo(mapperClass, routing.table(), routing.dependColumn(), routingFormatter);
                });
                // 从mapperStatement中提取将要执行的方法
                Method targetMethod = extractMethod(mapperClass, mappedStatement);
                // 从参数parameter中提取路由参数解析为tail
                String tail = extractTail(args[1], targetMethod, routingInfo);
                routingInfo.setCurrentTail(tail);
                RoutingSchemaInterceptor.setCurrentInfo(routingInfo);
            }
        }
        Object result = method.invoke(target, args);
        // clear
        RoutingSchemaInterceptor.removeCurrentInfo();
        return result;
    }

    private String extractTail(Object args, Method targetMethod, RoutingInfo routingInfo) {
        String fieldName = routingInfo.getDependColumn();
        RoutingParam.ParamType paramType = RoutingParam.ParamType.SINGLE;
        int paramIndex = 1;
        RoutingParam routingParam = targetMethod.getDeclaredAnnotation(RoutingParam.class);
        if (routingParam != null) {
            // 从注解中获取参数信息
            String fieldInRoutingParam = routingParam.field();
            if (StringUtils.isNotEmpty(fieldInRoutingParam)) {
                fieldName = fieldInRoutingParam;
            }
            paramType = routingParam.paramType();
            paramIndex = routingParam.paramIndex() + 1;
        }
        if (args instanceof Map) {
            Map argAsMap = (Map) args;
            String key = MYBATIS_GENERIC_NAME_PREFIX + paramIndex;
            if (argAsMap.containsKey(key)) {
                args = argAsMap.get(key);
            }
        } else if (args instanceof Collection || args.getClass().isArray()) {
            throw new RuntimeException("routing param can not be array|collection");
        } else if (paramIndex > 1) {
            throw new RuntimeException("When paramIndex > 1,mybatis warp param must be map");
        }
        return extractTailInParam(args, fieldName, paramType, routingInfo.getRoutingFormatter())
                .orElseThrow(RuntimeException::new);
    }

    @SneakyThrows
    private Optional<String> extractTailInParam(Object param, String fieldName, RoutingParam.ParamType paramType, RoutingFormatter routingFormatter) {
        if (param == null) {
            return Optional.empty();
        }
        Object targetParam = null;
        if (paramType != null) {
            switch (paramType) {
                case SINGLE: {
                    targetParam = param;
                    break;
                }
                case MAP: {
                    targetParam = ((Map) param).get(fieldName);
                    break;
                }
                case OBJECT: {
                    Class<?> paramClass = param.getClass();
                    Optional<Method> method = ReflectUtils.getAllWriterMethod(paramClass, Object.class).entrySet().stream()
                            .filter(entry -> entry.getKey().equals(fieldName))
                            .map(Map.Entry::getValue)
                            .findFirst();
                    if (method.isPresent()) {
                        targetParam = method.get().invoke(param);
                    }
                }
            }
        } else {

        }
        return targetParam == null ? Optional.empty() :
                Optional.of(routingFormatter.format(targetParam));
    }

    private Class<?> extractClass(MappedStatement mappedStatement) {
        String mappedStatementId = mappedStatement.getId();
        int index = mappedStatementId.lastIndexOf(".");
        String className = mappedStatementId.substring(0, index);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("解析mappedStatement异常,id:" + mappedStatementId);
        }
        return null;
    }

    private Method extractMethod(Class<?> mapperClass, MappedStatement mappedStatement) {
        String mappedStatementId = mappedStatement.getId();
        return METHOD_CACHE.computeIfAbsent(mappedStatementId, id -> {
            int index = mappedStatementId.lastIndexOf(".");
            String methodName = mappedStatementId.substring(index + 1);
            return Stream.of(mapperClass.getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst().orElse(null);
        });
    }
}
