package com.cheney.utils.mybatis.chain;

public class PackageSwitch extends AbstractSwitch {

    private final static String target = "@\\{package}";

    public PackageSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return clazz.getCanonicalName();
    }

}
