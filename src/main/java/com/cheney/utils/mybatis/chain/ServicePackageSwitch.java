package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

public class ServicePackageSwitch extends AbstractSwitch {

    private final static String target = "@\\{servicePackage}";

    public ServicePackageSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        String pack = XMLGenerator.getGeneratorFilePackage().get(XMLGenerator.getCurrentKey());
        return toJavaPackage(pack);
    }

}
