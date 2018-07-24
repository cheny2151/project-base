package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

public class IdTypeSwitch extends AbstractSwitch {

    private final static String target = "@\\{idType}";

    public IdTypeSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return XMLGenerator.ID_TYPE.toString();
    }
}
