package com.cheney.utils.mybatis.chain;

import com.cheney.utils.BeanUtils;
import com.cheney.utils.mybatis.XMLGenerator;

import java.util.Set;

public class SetValueSwitch extends AbstractSwitch {

    private final static String target = "@\\{setValue}";

    public SetValueSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        Set<String> names = BeanUtils.getAllFieldNames(clazz);
        StringBuilder setValue = new StringBuilder();
        names.remove(XMLGenerator.ID_COLUMN);
        for (String s : names) {
            String column = underline(s);
            setValue.append(column).append(EQUAL_TO).append(placeholder(s)).append(SEPARATOR);
        }
        return setValue.subSequence(0, setValue.length() - 1).toString();
    }

}
