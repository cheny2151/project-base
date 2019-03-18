package com.cheney.mybatis.chain;

import com.cheney.mybatis.XMLGenerator;

public class IdColumnSwitch extends AbstractSwitch {

    private final static String target = "@\\{idColumn}";

    public IdColumnSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return XMLGenerator.ID_COLUMN;
    }

}
