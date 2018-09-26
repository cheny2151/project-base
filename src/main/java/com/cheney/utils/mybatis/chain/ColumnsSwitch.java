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
        for (String s : names) {
            if (!XMLGenerator.HUMP) {
                s = underline(s);
            }
            columns.append(s).append(SEPARATOR);
        }
        return columns.subSequence(0, columns.length() - 1).toString();
    }
}
