package com.cheney.system.databind;

import java.beans.PropertyEditorSupport;

/**
 * Created by cheny on 2017/9/21.
 */
public class StringEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        return super.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            String value = text.trim();
            if ("".equals(value)) {
                setValue(null);
            } else {
                setValue(value);
            }
        } catch (Exception e) {
            setValue(null);
        }
    }
}
