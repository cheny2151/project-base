package com.cheney.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateUtils {

    private final static String[] FORMATSTR = new String[]{"yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd"
            , "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss"};

    private final static ThreadLocal<SimpleDateFormat> DEFAULT_FORMAT = new ThreadLocal<>();

    private final static ThreadLocal<SimpleDateFormat> DAY_FORMAT = new ThreadLocal<>();

    public static Date parseDate(String date) {
        try {
            return org.apache.commons.lang.time.DateUtils.parseDate(date, FORMATSTR);
        } catch (ParseException e) {
            throw new IllegalArgumentException("ParseException for " + date + ",pattern is " + JSON.toJSONString(FORMATSTR), e);
        }
    }

    public static String formatDate(Date date) {
        return getDateFormat(DEFAULT_FORMAT, "yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String formatDay(Date date) {
        return getDateFormat(DAY_FORMAT, "yyyy-MM-dd").format(date);
    }

    public static Date toDate(Object time) {
        if (time instanceof String) {
            try {
                return org.apache.commons.lang3.time.DateUtils.parseDate((String) time, FORMATSTR);
            } catch (ParseException e) {
                log.error("时间解析错误", e);
            }
        }
        if (time instanceof Date) {
            return (Date) time;
        }
        if (time instanceof Long) {
            return new Date((Long) time);
        }
        throw new IllegalArgumentException();
    }


    private static SimpleDateFormat getDateFormat(ThreadLocal<SimpleDateFormat> formatThreadLocal, String pattern) {
        SimpleDateFormat simpleDateFormat = formatThreadLocal.get();
        if (simpleDateFormat == null) {
            formatThreadLocal.set(simpleDateFormat = new SimpleDateFormat(pattern));
        }
        return simpleDateFormat;
    }

}
