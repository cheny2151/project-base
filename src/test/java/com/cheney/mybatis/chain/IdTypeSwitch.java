package com.cheney.mybatis.chain;

import com.cheney.mybatis.XMLGenerator;

public class IdTypeSwitch extends AbstractSwitch {

    private final static String target = "@\\{idType}";

    public IdTypeSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return XMLGenerator.ID_TYPE.getSimpleName();
    }
}
