package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

public class ServiceImportImplSwitch extends AbstractSwitch {

    private final static String target = "@\\{serviceImportImpl}";

    public ServiceImportImplSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return "import " + XMLGenerator.getGeneratorFilePackage().get(XMLGenerator.getCurrentKey()) + System.getProperty("line.separator");
    }

}
