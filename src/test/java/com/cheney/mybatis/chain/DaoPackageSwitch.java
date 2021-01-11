package com.cheney.mybatis.chain;

import com.cheney.mybatis.XMLGenerator;

import java.io.File;

public class DaoPackageSwitch extends AbstractSwitch {

    private final static String target = "@\\{daoPackage}";

    public DaoPackageSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        String pack = XMLGenerator.getGeneratorFilePackage().get(XMLGenerator.getCurrentKey());
        return pack.replaceAll(File.separator, ".").replaceAll("src.main.java.", "");
    }

}
