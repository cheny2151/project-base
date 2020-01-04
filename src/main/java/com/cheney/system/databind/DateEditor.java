package com.cheney.system.databind;

import com.cheney.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * Created by cheny on 2017/9/21.
 */
@Slf4j
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
        } catch (Exception e) {
            setValue(null);
            log.error("解析并设置请求时间参数失败", e);
        }
    }
}
