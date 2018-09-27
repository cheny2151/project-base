package com.cheney.utils.mybatis.chain;

public class ColumnsSwitch extends ColumnFliedAbstractSwitch {

    private final static String target = "@\\{cloumns}";

    public ColumnsSwitch(Switch next) {
        super(next, target);
    }

    String ifFlag(String field, String column, boolean last) {
        StringBuilder content = new StringBuilder("\t\t<if test=\"" + field + " != null\">");
        content.append(LINE_BREAK).append("\t\t\t").append(column);
        if (!last) {
            content.append(SEPARATOR).append(LINE_BREAK).append("\t\t</if>").append(LINE_BREAK);
        } else {
            content.append(LINE_BREAK).append("\t\t</if>");
        }
        return content.toString();
    }

}
