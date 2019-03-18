package com.cheney.mybatis.chain;

import com.cheney.mybatis.XMLGenerator;

public class ServiceImportImplSwitch extends AbstractSwitch {

    private final static String target = "@\\{serviceImportImpl}";

    public ServiceImportImplSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        String separator = System.getProperty("line.separator");
        String daoPackage = toJavaPackage(XMLGenerator.getGeneratorFilePackage().get("Dao"));
        String servicePackage = toJavaPackage(XMLGenerator.getGeneratorFilePackage().get("Service"));
        return "import " + daoPackage + ".BaseMapper;" + separator
                + "import " + daoPackage + "." + clazz.getSimpleName() + "Mapper;" + separator
                + "import " + clazz.getCanonicalName() + ";" + separator
                + "import " + servicePackage + "." + clazz.getSimpleName() + "Service";
    }

}
