package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

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
