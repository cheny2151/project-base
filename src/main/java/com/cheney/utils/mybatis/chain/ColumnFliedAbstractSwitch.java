package com.cheney.utils.mybatis.chain;

import com.cheney.utils.BeanUtils;
import com.cheney.utils.mybatis.XMLGenerator;

import java.util.Set;

public abstract class ColumnFliedAbstractSwitch extends AbstractSwitch {

    public ColumnFliedAbstractSwitch(Switch next, String target) {
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

    abstract String ifFlag(String field, String column, boolean last);

}
