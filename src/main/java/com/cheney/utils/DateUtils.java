package com.cheney.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private final static String[] FORMATSTR = new String[]{"yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd"
            , "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss"};

    private final static SimpleDateFormat DEFAULT_FORMAT;

    private final static SimpleDateFormat DAY_FORMAT;

    static {
        DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    }

    public static Date parseDate(String date) throws ParseException {
        return org.apache.commons.lang.time.DateUtils.parseDate(date, FORMATSTR);
    }

    public static String formatDate(Date date) {
        return DEFAULT_FORMAT.format(date);
    }

    public static String formatDay(Date date) {
        return DAY_FORMAT.format(date);
    }

}
