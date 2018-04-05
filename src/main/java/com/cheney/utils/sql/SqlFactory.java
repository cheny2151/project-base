package com.cheney.utils.sql;

import org.apache.commons.lang.StringUtils;

/**
 * sql拼接工厂
 */
public class SqlFactory {

    private final static String FROM = "from";

    private final static String SELECT = "select";

    private final static String WHERE = "where";

    private final static String SPACE = " ";

    private final static String SIMPLE_TABLE_HEAD = "t";

    private final static String SEPARATOR = ",";

    private final static String COUNT = "count(*)";

    /**
     * 创建查询sql:表名缩写从t0开始
     *
     * @param selection   查找内容
     * @param tableNames  表名
     * @param restriction 限制
     * @return sql
     */
    public static String createSelect(String selection, String[] tableNames, String restriction) {
        if (StringUtils.isEmpty(selection) || tableNames == null || tableNames.length == 0) {
            throw new IllegalArgumentException("illegal sql args");
        }
        StringBuilder from = new StringBuilder();
        for (int i = 0; i < tableNames.length; i++) {
            from.append(tableNames[i]).append(SPACE).append(SIMPLE_TABLE_HEAD).append(i).append(SEPARATOR);
        }
        return from.deleteCharAt(from.length() - 1).insert(0, SPACE)
                .insert(0, FROM).insert(0, SPACE)
                .insert(0, selection).insert(0, SPACE)
                .insert(0, SELECT).append(SPACE)
                .append(WHERE).append(SPACE)
                .append(restriction).toString();
    }

    /**
     * count
     */
    public static String createCount(String[] tableNames, String restriction) {
        if (tableNames == null || tableNames.length == 0) {
            throw new IllegalArgumentException("illegal sql args");
        }
        StringBuilder from = new StringBuilder();
        for (int i = 0; i < tableNames.length; i++) {
            from.append(tableNames[i]).append(SPACE).append(SIMPLE_TABLE_HEAD).append(i).append(SEPARATOR);
        }
        return from.deleteCharAt(from.length() - 1).insert(0, SPACE)
                .insert(0, FROM).insert(0, SPACE)
                .insert(0, COUNT).insert(0, SPACE)
                .insert(0, SELECT).append(SPACE)
                .append(WHERE).append(SPACE)
                .append(restriction).toString();
    }

    /**
     * query参数函数式接口
     */
    public interface ParameterHolder {

        void setParameter(Query query);

    }

}
