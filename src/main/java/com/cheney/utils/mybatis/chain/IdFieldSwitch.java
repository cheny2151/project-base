package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

public class IdFieldSwitch extends AbstractSwitch {

    private final static String target = "@\\{idField}";

    public IdFieldSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return placeholder(XMLGenerator.ID);
    }
}
