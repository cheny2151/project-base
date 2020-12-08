package com.cheney.utils.mybatis.plugin.routing.format;

import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 格式化为月份
 *
 * @author cheney
 * @date 2020-11-29
 */
public class MonthRoutingFormatter implements RoutingFormatter {

    @Override
    @SneakyThrows
    public String format(Object param) {
        String f;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        if (param instanceof Date) {
            f = dateFormat.format(param);
        } else {
            f = dateFormat.format(DateUtils.parseDate(param.toString(), com.cheney.utils.DateUtils.FORMATSTR));
        }
        return f;
    }
}
