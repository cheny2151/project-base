package com.cheney.mybatis.chain;

import cn.cheny.toolbox.property.PropertyNameUtils;
import com.cheney.mybatis.XMLGenerator;
import cn.cheny.toolbox.reflect.ReflectUtils;

import java.util.Set;

public abstract class ColumnFieldAbstractSwitch extends AbstractSwitch {

    public ColumnFieldAbstractSwitch(Switch next, String target) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        StringBuilder columns = new StringBuilder();
        Set<String> names = ReflectUtils.getAllFieldNames(clazz);
        names.remove(XMLGenerator.ID);
        int count = 1;
        int size = names.size();
        for (String s : names) {
            String column = s;
            String next;
            if (!XMLGenerator.HUMP) {
                column = PropertyNameUtils.underline(s);
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

    abstract String ifFlag(String field, String column, boolean last);

}
