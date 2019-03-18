package com.cheney.mybatis.chain;

public class LowSimpleClassNameSwitch extends AbstractSwitch {

    private final static String target = "@\\{lowSimpleClassName}";

    public LowSimpleClassNameSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
    }

}
