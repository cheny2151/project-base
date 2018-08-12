package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

public class DaoPackageSwitch extends AbstractSwitch {

    private final static String target = "@\\{daoPackage}";

    public DaoPackageSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        String pack = XMLGenerator.getGeneratorFilePackage().get(XMLGenerator.getCurrentKey());
        return pack.replaceAll("\\\\", ".").replaceAll("src.main.java.", "");
    }

}
