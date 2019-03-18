package com.cheney.mybatis.chain;

import com.cheney.utils.ReflectUtils;
import com.cheney.mybatis.XMLGenerator;

import java.util.Set;

public class ResultMapSwitch extends AbstractSwitch {

    private final static String target = "@\\{resultMap}";

    public ResultMapSwitch() {
    }

    public ResultMapSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        Set<String> fieldNames = ReflectUtils.getAllFieldNames(clazz);
        StringBuilder resultMap = new StringBuilder();
        String separator = System.getProperty("line.separator");
        resultMap.append("<id property=\"").append(XMLGenerator.ID).append("\" column=\"")
                .append(XMLGenerator.ID_COLUMN).append("\"/>").append(separator);
        String column;
        for (String field : fieldNames) {
            column = field;
            if (!XMLGenerator.HUMP) {
                column = underline(field);
            }
            if (XMLGenerator.ID.equalsIgnoreCase(column)) {
                continue;
            }
            resultMap.append("        <result property=\"").append(field).append("\" column=\"").append(column).append("\"/>").append(separator);
        }
        return resultMap.subSequence(0, resultMap.length() - separator.length()).toString();
    }


}
