package com.cheney.mybatis.chain;

import com.cheney.mybatis.XMLGenerator;

public class IdSimpleClassNameSwitch extends AbstractSwitch {

    private final static String target = "@\\{idSimpleClassName}";

    public IdSimpleClassNameSwitch(Switch next){
        super(next,target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return XMLGenerator.ID_TYPE.getSimpleName();
    }

}
