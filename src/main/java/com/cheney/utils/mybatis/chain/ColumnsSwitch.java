package com.cheney.utils.mybatis.chain;

import com.cheney.utils.BeanUtils;
import com.cheney.utils.mybatis.XMLGenerator;

import java.util.Set;

public class ColumnsSwitch extends AbstractSwitch {

    private final static String target = "@\\{cloumns}";

    public ColumnsSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        StringBuilder columns = new StringBuilder();
        Set<String> names = BeanUtils.getAllFieldNames(clazz);
        names.remove(XMLGenerator.ID);
        int count = 1;
        int size = names.size();
        for (String s : names) {
            String column = s;
            String next;
            if (!XMLGenerator.HUMP) {
                column = underline(s);
            }
            if (count != size) {
                next = ifFlag(s, column, false);
            } else {
                next = ifFlag(s, column, true);
            }
            columns.append(next);
            count++;
        }
        return columns.toString();
    }

    private String ifFlag(String field, String column, boolean last) {
        StringBuilder content = new StringBuilder("<if test=\"" + field + "!=null\">");
        content.append(LINE_BREAK).append("  ").append(column);
        if (!last) {
            content.append(SEPARATOR);
        }
        content.append(LINE_BREAK).append("</if>");
        return content.toString();
    }
}
