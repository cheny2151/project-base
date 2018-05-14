package com.cheney.system.databind;

import com.cheney.utils.DateUtils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by cheny on 2017/9/21.
 */
public class DateEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return DateUtils.formatDate(value);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            String value = text.trim();
            if ("".equals(value)) {
                setValue(null);
            } else {
                Date date = DateUtils.parseDate(value);
                setValue(date);
            }
        } catch (ParseException e) {
            setValue(null);
            e.printStackTrace();
        }
    }
}
