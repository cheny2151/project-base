package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

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
