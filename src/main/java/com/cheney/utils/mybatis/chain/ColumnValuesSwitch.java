package com.cheney.utils.mybatis.chain;

import com.cheney.utils.BeanUtils;
import com.cheney.utils.mybatis.XMLGenerator;

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
        names.remove(XMLGenerator.ID_COLUMN);
        for (String s : names) {
            if (!XMLGenerator.HUMP) {
                s = underline(s);
            }
            columnValues.append(placeholder(s)).append(SEPARATOR);
        }
        columnValues.subSequence(0, columnValues.length() - 1);
        return columnValues.toString();
    }

}
