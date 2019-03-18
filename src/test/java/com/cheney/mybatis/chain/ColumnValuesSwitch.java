package com.cheney.mybatis.chain;

import com.cheney.utils.BeanUtils;
import com.cheney.mybatis.XMLGenerator;

import java.util.Set;

public class ColumnValuesSwitch extends AbstractSwitch {

    private final static String target = "@\\{columnValues}";

    public ColumnValuesSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        StringBuilder columnValues = new StringBuilder();
        Set<String> names = BeanUtils.getAllFieldNames(clazz);
        names.remove(XMLGenerator.ID);
        int size = names.size();
        int count = 1;
        for (String s : names) {
            String next;
            if (size != count) {
                next = ifFlag(s, false);
            } else {
                next = ifFlag(s, true);
            }
            columnValues.append(next);
            count++;
        }
        return columnValues.toString();
    }

    private String ifFlag(String field, boolean last) {
        StringBuilder content = new StringBuilder("\t\t<if test=\"" + field + " != null\">");
        content.append(LINE_BREAK).append("\t\t\t").append(placeholder(field));
        if (!last) {
            content.append(SEPARATOR).append(LINE_BREAK).append("\t\t</if>").append(LINE_BREAK);
        } else {
            content.append(LINE_BREAK).append("\t\t</if>");
        }
        return content.toString();
    }
}
