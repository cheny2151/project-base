package com.cheney.utils.mybatis.plugin.routing;

import cn.cheny.toolbox.reflect.ReflectUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分表路由插件
 *
 * @author cheney
 * @date 2020-11-29
 */
public class RoutingSchemaInterceptor implements Interceptor {

    private static final Class<StatementHandler> TARGET_INTERFACE = StatementHandler.class;

    private static final ThreadLocal<RoutingInfo> currentRouting = new ThreadLocal<>();

    private static final Field INVOKER = ReflectUtils.field(BoundSql.class, "sql");

    public static final String AND = "_";

    // table截取正则,from|join|,前断言匹配
    private String patternStr = "(?<=from\\s|join\\s|,)(\\s*%s\\w*)";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        throw new UnsupportedOperationException();
    }

    @Override
    @SneakyThrows
    public Object plugin(Object target) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length == 0 || !ArrayUtils.contains(interfaces, TARGET_INTERFACE)) {
            return target;
        }
        RoutingInfo routingInfo = currentRouting.get();
        if (routingInfo != null) {
            String table = routingInfo.getTable();
            BoundSql boundSql = ((StatementHandler) target).getBoundSql();
            String sql = boundSql.getSql();
            sql = replace(sql, table, routingInfo.getCurrentTail());
            INVOKER.set(boundSql, sql);
        }
        return target;
    }

    /**
     * 通过正则匹配，替换原table为table+"_"+tail
     *
     * @param sql   原sql
     * @param table 表名
     * @param tail  尾补充
     * @return 替换完的sql
     */
    private String replace(String sql, String table, String tail) {
        tail = AND + tail;
        String patternStr = String.format(this.patternStr, table);
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(sql);
        String[] others = pattern.split(sql);
        int i = 0;
        StringBuilder sqlBuilder = new StringBuilder(others[i++]);
        // 正则匹配
        while (matcher.find()) {
            sqlBuilder.append(matcher.group()).append(tail);
            sqlBuilder.append(others[i++]);
        }
        for (; i < others.length; i++) {
            sqlBuilder.append(others[i]);
        }
        return sqlBuilder.toString();
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public static void setCurrentInfo(RoutingInfo routingInfo) {
        currentRouting.set(routingInfo);
    }

    public static void removeCurrentInfo() {
        currentRouting.remove();
    }
}
