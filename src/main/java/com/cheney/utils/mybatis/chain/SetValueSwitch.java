package com.cheney.utils.mybatis.chain;

public class SetValueSwitch extends ColumnFieldAbstractSwitch {

    private final static String target = "@\\{setValue}";

    public SetValueSwitch(Switch next) {
        super(next, target);
    }

    String ifFlag(String field, String column, boolean last) {
        StringBuilder content = new StringBuilder("\t\t<if test=\"" + field + " != null\">");
        content.append(LINE_BREAK)
                .append("\t\t\t").append(column).append(EQUAL_TO).append(placeholder(field));
        if (!last) {
            content.append(SEPARATOR).append(LINE_BREAK).append("\t\t</if>").append(LINE_BREAK);
        } else {
            content.append(LINE_BREAK).append("\t\t</if>");
        }
        return content.toString();
    }

}
