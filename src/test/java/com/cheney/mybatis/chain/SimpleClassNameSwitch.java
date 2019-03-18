package com.cheney.mybatis.chain;

public class SimpleClassNameSwitch extends AbstractSwitch {

    private final static String target = "@\\{simpleClassName}";

    public SimpleClassNameSwitch(Switch next){
        super(next,target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return clazz.getSimpleName();
    }

}
