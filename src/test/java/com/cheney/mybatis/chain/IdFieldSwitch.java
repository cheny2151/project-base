package com.cheney.mybatis.chain;

import com.cheney.mybatis.XMLGenerator;

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
