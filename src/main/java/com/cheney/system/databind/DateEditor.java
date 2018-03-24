package com.cheney.system.databind;

import org.apache.commons.lang.time.DateUtils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cheny on 2017/9/21.
 */
public class DateEditor extends PropertyEditorSupport {

    public static final String[] DATE_PATTERNS = new String[]{"yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss"};

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return SIMPLE_DATE_FORMAT.format(value);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            String value = text.trim();
            if ("".equals(value)) {
                setValue(null);
            } else {
                Date date = DateUtils.parseDate(value, DATE_PATTERNS);
                setValue(date);
            }
        } catch (ParseException e) {
            setValue(null);
            e.printStackTrace();
        }
    }
}
